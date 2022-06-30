package com.example.rickandmorty.data.pagination.episodes

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.episodes.EpisodeEntity
import com.example.rickandmorty.data.local.database.episodes.remote_keys.EpisodeRemoteKeys
import com.example.rickandmorty.data.mapper.episode.EpisodeDtoToEpisodeEntityMapper
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.domain.models.episode.EpisodeFilter


@OptIn(ExperimentalPagingApi::class)
class EpisodesFiltersMediator(
    private val api: EpisodesApi,
    private val database: RickAndMortyDatabase,
    private val episodeFilters: EpisodeFilter
) : RemoteMediator<Int, EpisodeEntity>() {

    private val episodesDao = database.episodesDao
    private val episodesRemoteKeysDao = database.episodesRemoteKeysDao

    private var startingPage = 1

    private var hiddenEpisodes = listOf<EpisodeEntity>()

    override suspend fun initialize(): InitializeAction {
        println("...............initialize episodesFilterMediator.............")

        hiddenEpisodes = episodesDao.getHiddenEpisodes()

        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
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
                "name" to episodeFilters.name,
                "episode" to episodeFilters.episode
            ).filter { it.value != null }


            val episodesApiResponse = api.getPagedEpisodesByFilters(
                page = page,
                filters = filtersToApply
            )

            val episodesFromApi = episodesApiResponse.results

            println("Hidden episodes: ${hiddenEpisodes.map { it.id }}")

            val hiddenEpisodesIds = hiddenEpisodes.map { it.id }

            val idsOfHiddenEpisodesList = mutableListOf<Int>()
            episodesFromApi.forEach { episode ->
                val containsCharacter = hiddenEpisodesIds.contains(-episode.id)
                if (containsCharacter) idsOfHiddenEpisodesList.add(-episode.id)
            }

            //       println("Ids: ${idsOfHiddenCharactersList}")
            episodesDao.deleteEpisodesByIds(idsOfHiddenEpisodesList)
            episodesRemoteKeysDao.deleteKeysByIds(idsOfHiddenEpisodesList)

            val isEndOfList = episodesFromApi.isEmpty()
                    || episodesApiResponse.info?.next.isNullOrBlank()
                    || episodesApiResponse.toString().contains("error")


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
            println("Episodes from api: ${episodesFromApi.map { "${it.id}: ${it.name}" }}")
            println("List size: ${episodesFromApi.size}")

            database.withTransaction {
                val keys = episodesFromApi.map {
                    var prevKey: Int? = (it.id - 1) / 20
                    if (prevKey != null && prevKey <= 0) prevKey = null
                    val nextKey = prevKey?.plus(2) ?: 2

                    EpisodeRemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                episodesRemoteKeysDao.insertKeys(keys)

                val mapperDtoToEntity = EpisodeDtoToEpisodeEntityMapper()
                val episodesEntities = episodesFromApi.map { mapperDtoToEntity.map(it) }
                episodesDao.insertEpisodes(episodesEntities)

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
        state: PagingState<Int, EpisodeEntity>,
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

    private suspend fun getFirstRemoteKey(
        state: PagingState<Int, EpisodeEntity>
    ): EpisodeRemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { character ->
                episodesRemoteKeysDao.remoteKeysEpisodesId(character.id)
            }
    }


    private suspend fun getLastRemoteKey(
        state: PagingState<Int, EpisodeEntity>,
    ): EpisodeRemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { character ->
                episodesRemoteKeysDao.remoteKeysEpisodesId(character.id)
            }
    }

    private suspend fun getClosestRemoteKey(
        state: PagingState<Int, EpisodeEntity>
    ): EpisodeRemoteKeys? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    episodesRemoteKeysDao.remoteKeysEpisodesId(id)
                }
            }
    }
}