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

            val hiddenEpisodesIds = hiddenEpisodes.map { it.id }

            val idsOfHiddenEpisodesList = mutableListOf<Int>()
            episodesFromApi.forEach { episode ->
                val containsCharacter = hiddenEpisodesIds.contains(-episode.id)
                if (containsCharacter) idsOfHiddenEpisodesList.add(-episode.id)
            }

            episodesDao.deleteEpisodesByIds(idsOfHiddenEpisodesList)
            episodesRemoteKeysDao.deleteKeysByIds(idsOfHiddenEpisodesList)

            val isEndOfList = episodesFromApi.isEmpty()
                    || episodesApiResponse.info?.next.isNullOrBlank()
                    || episodesApiResponse.toString().contains("error")

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
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
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
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                } else {
                    return nextKey
                }
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey != null) {
                    return prevKey
                } else {
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