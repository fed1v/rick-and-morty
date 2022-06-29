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
import com.example.rickandmorty.domain.models.character.CharacterFilter

@OptIn(ExperimentalPagingApi::class)
class CharactersFiltersMediator(
    private val api: CharactersApi,
    private val database: RickAndMortyDatabase,
    private val characterFilters: CharacterFilter
) : RemoteMediator<Int, CharacterEntity>() {

    private val charactersDao = database.charactersDao
    private val charactersRemoteKeysDao = database.charactersRemoteKeysDao

    private var startingPage = 1

    private var hiddenCharacters = listOf<CharacterEntity>()

    override suspend fun initialize(): InitializeAction {
        println("Initialize Characters Filter Mediator")

        hiddenCharacters = charactersDao.getHiddenCharacters()

        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {

        val keyPageData = getKeyPageData(loadType, state)
        val page = when (keyPageData) {
            is MediatorResult.Success -> {
                return keyPageData
            }
            else -> {
                //        keyPageData as Int
                startingPage++
            }
        }

        try {
            val filtersToApply = mapOf(
                "name" to characterFilters.name,
                "status" to characterFilters.status,
                "species" to characterFilters.species,
                "type" to characterFilters.type,
                "gender" to characterFilters.gender
            ).filter { it.value != null }


            val charactersApiResponse = api.getPagedCharactersByFilters(
                page = page,
                filters = filtersToApply
            )

            val charactersFromApi = charactersApiResponse.results

            println("Hidden characters: ${hiddenCharacters.map { it.id }}")

            val hiddenCharactersIds = hiddenCharacters.map { it.id }

            val idsOfHiddenCharactersList = mutableListOf<Int>()
            charactersFromApi.forEach { character ->
                val containsCharacter = hiddenCharactersIds.contains(-character.id)
                if (containsCharacter) idsOfHiddenCharactersList.add(-character.id)
            }

            //       println("Ids: ${idsOfHiddenCharactersList}")
            charactersDao.deleteCharactersByIds(idsOfHiddenCharactersList)
            charactersRemoteKeysDao.deleteKeysByIds(idsOfHiddenCharactersList)

            val isEndOfList = charactersFromApi.isEmpty()
                    || charactersApiResponse.info.next.isBlank()
                    || charactersApiResponse.toString().contains("error")


            val prevKey = //if (page == 1) null else
                page - 1
            val nextKey = //if (isEndOfList) null else
                page + 1


            println("________")
            println("Page: ${page}")
            println("prevKey: $prevKey")
            println("nextKey: $nextKey")
            println("________")


            println("IsEndOfList? $isEndOfList")
            println("Characters from api: ${charactersFromApi.map { "${it.id}: ${it.name}" }}")
            println("List size: ${charactersFromApi.size}")

            database.withTransaction {
                val keys = charactersFromApi.map {
                    var prevKey: Int? = (it.id - 1) / 20
                    if (prevKey != null && prevKey <= 0) prevKey = null
                    val nextKey = prevKey?.plus(2) ?: 2

                    RemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                charactersRemoteKeysDao.insertAll(keys)

                val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()
                val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }
                charactersDao.insertCharacters(charactersEntities)

                println("End")
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            println("ERRORRRR")
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
                println("Refesh: $remoteKeys")
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                println("Append: $remoteKeys")
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    println("Append: nextKey == null")
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                } else {
                    return nextKey
                }
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                println("Prepend: $remoteKeys")
                val prevKey = remoteKeys?.prevKey
                if (prevKey != null) {
                    return prevKey
                } else {
                    println("Prepend: nextKey == null")
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                }
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


    private suspend fun getLastRemoteKey(
        state: PagingState<Int, CharacterEntity>,
    ): RemoteKeys? {
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