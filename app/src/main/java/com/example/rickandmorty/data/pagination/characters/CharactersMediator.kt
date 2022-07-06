package com.example.rickandmorty.data.pagination.characters

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.data.local.database.characters.remote_keys.CharacterRemoteKeys
import com.example.rickandmorty.data.mapper.character.CharacterDtoToCharacterEntityMapper
import com.example.rickandmorty.data.remote.api.characters.CharactersApi


@OptIn(ExperimentalPagingApi::class)
class CharactersMediator(
    private val api: CharactersApi,
    private val database: RickAndMortyDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    private val charactersDao = database.charactersDao
    private val charactersRemoteKeysDao = database.charactersRemoteKeysDao

    private var startPaginationFromIndex: Int = -1

    private var charactersToRemember = mutableListOf<CharacterEntity>()
    private var keysToRemember = mutableListOf<CharacterRemoteKeys>()

    private var hiddenCharacters = mutableListOf<CharacterEntity>()
    private var hiddenKeys = mutableListOf<CharacterRemoteKeys>()

    override suspend fun initialize(): InitializeAction {
        var rememberFromIndex = -1
        val keys = charactersRemoteKeysDao.getAllKeys()

        for (i in keys.indices) {
            if (i + 1 <= keys.size - 1) {
                if ((keys[i]?.id ?: -1) > 0
                    && (keys[i + 1]?.id ?: -1) > 0
                    && keys[i]?.id != keys[i + 1]?.id?.minus(1)
                ) {
                    rememberFromIndex = keys[i]?.id ?: -1
                    startPaginationFromIndex = keys[i]?.id ?: -1
                    break
                }
            }
        }

        if (keys.isNotEmpty() && rememberFromIndex == -1) {
            if ((keys[keys.lastIndex]?.id?.rem(20) ?: -1) != 0) {
                rememberFromIndex = keys[keys.lastIndex]?.id ?: 0
                rememberFromIndex /= 20
                rememberFromIndex *= 20
            }
        }

        database.withTransaction {
            val hidCharacters = charactersDao.getHiddenCharacters()
            hiddenCharacters.addAll(hidCharacters)

            val hidKeys = charactersRemoteKeysDao.getHiddenKeys()
            hiddenKeys.addAll(hidKeys)
        }


        if (rememberFromIndex != -1) {
            rememberFromIndex /= 20
            rememberFromIndex *= 20

            database.withTransaction {
                val charactersToDelete = charactersDao.getCharactersFromId(rememberFromIndex)
                charactersToRemember.addAll(charactersToDelete)

                val keysToDelete = charactersRemoteKeysDao.getKeysFromId(rememberFromIndex)
                keysToRemember.addAll(keysToDelete)

                val charactersToHide = charactersToDelete.map { it.copy(id = -it.id) }
                charactersDao.insertCharacters(charactersToHide)

                val keysToHide = keysToDelete.map { it.copy(id = -it.id) }
                charactersRemoteKeysDao.insertKeys(keysToHide)

                charactersDao.deleteCharactersFromId(rememberFromIndex)
                charactersRemoteKeysDao.deleteKeysFromId(rememberFromIndex)
            }
        }

        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {

        val pageKeyData = getKeyPageData(loadType, state)
        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val charactersApiResponse = api.getPagedCharacters(page = page)
            val charactersFromApi = charactersApiResponse.results

            val isEndOfList = charactersFromApi.isEmpty()
                    || charactersApiResponse.info?.next.isNullOrBlank()
                    || charactersApiResponse.toString().contains("error")


            database.withTransaction {
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = charactersFromApi.map {
                    CharacterRemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                charactersRemoteKeysDao.insertKeys(keys)

                val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()
                val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }

                charactersDao.insertCharacters(charactersEntities)

                if (isEndOfList) {
                    charactersRemoteKeysDao.insertKeys(hiddenKeys)
                    charactersDao.insertCharacters(hiddenCharacters)

                    charactersDao.clearHiddenCharacters()
                    charactersRemoteKeysDao.clearHiddenKeys()
                }
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            database.withTransaction {
                if (hiddenCharacters.isEmpty()) {
                    hiddenCharacters.addAll(charactersDao.getHiddenCharacters())
                }
                if (hiddenKeys.isEmpty()) {
                    hiddenKeys.addAll(charactersRemoteKeysDao.getHiddenKeys())
                }

                charactersDao.insertCharacters(charactersToRemember)
                charactersRemoteKeysDao.insertKeys(keysToRemember)

                charactersDao.insertCharacters(hiddenCharacters.map { it.copy(id = -it.id) })
                charactersRemoteKeysDao.insertKeys(hiddenKeys.map { it.copy(id = -it.id) })

                charactersDao.clearHiddenCharacters()
                charactersRemoteKeysDao.clearHiddenKeys()
            }

            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                return nextKey ?: MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                return prevKey ?: MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { character ->
                charactersRemoteKeysDao.remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun getLastRemoteKey(
        state: PagingState<Int, CharacterEntity>,
    ): CharacterRemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { character ->
                charactersRemoteKeysDao.remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    charactersRemoteKeysDao.remoteKeysCharacterId(id)
                }
            }
    }
}
