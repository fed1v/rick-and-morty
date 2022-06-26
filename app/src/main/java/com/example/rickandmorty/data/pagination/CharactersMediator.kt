package com.example.rickandmorty.data.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.data.mapper.character.CharacterDtoToCharacterEntityMapper
import com.example.rickandmorty.data.remote.characters.CharactersApi


@OptIn(ExperimentalPagingApi::class)
class CharactersMediator(
    private val api: CharactersApi,
    private val database: RickAndMortyDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    private val charactersDao = database.charactersDao
    private val charactersRemoteKeysDao = database.charactersRemoteKeysDao

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
                    || charactersApiResponse.info.next.isNullOrBlank()
                    || charactersApiResponse.toString().contains("error")

            println("IsEndOfList? $isEndOfList")
            println("Characters from api: ${charactersFromApi}")

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                //    charactersRemoteKeysDao.clearRemoteKeys()
                //    charactersDao.clearCharacters()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                println("________")
                println("Page: $page")
                println("prevKey: $prevKey")
                println("nextKey: $nextKey")
                println("________")


                val keys = charactersFromApi.map {
                    RemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                charactersRemoteKeysDao.insertAll(keys)

                val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()
                val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }

                println("Insert Characters: $charactersEntities")


                charactersDao.insertCharacters(charactersEntities)
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
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

    private suspend fun getFirstRemoteKey(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { character ->
                charactersRemoteKeysDao.remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { character ->
                charactersRemoteKeysDao.remoteKeysCharacterId(character.id)
            }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    charactersRemoteKeysDao.remoteKeysCharacterId(id)
                }
            }
    }
}
