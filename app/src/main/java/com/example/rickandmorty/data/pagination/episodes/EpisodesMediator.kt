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

@OptIn(ExperimentalPagingApi::class)
class EpisodesMediator(
    private val api: EpisodesApi,
    private val database: RickAndMortyDatabase
) : RemoteMediator<Int, EpisodeEntity>() {

    private val episodesDao = database.episodesDao
    private val episodesRemoteKeysDao = database.episodesRemoteKeysDao

    private var startPaginationFromIndex: Int = -1

    private var episodesToRemember = mutableListOf<EpisodeEntity>()
    private var keysToRemember = mutableListOf<EpisodeRemoteKeys>()

    private var hiddenEpisodes = mutableListOf<EpisodeEntity>()
    private var hiddenKeys = mutableListOf<EpisodeRemoteKeys>()


    override suspend fun initialize(): InitializeAction {
        var rememberFromIndex = -1

        val keys = episodesRemoteKeysDao.getAllKeys()

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
            val hidEpisodes = episodesDao.getHiddenEpisodes()
            hiddenEpisodes.addAll(hidEpisodes)

            val hidKeys = episodesRemoteKeysDao.getHiddenKeys()
            hiddenKeys.addAll(hidKeys)
        }


        if (rememberFromIndex != -1) {
            rememberFromIndex /= 20
            rememberFromIndex *= 20

            database.withTransaction {
                val episodesToDelete = episodesDao.getEpisodesFromId(rememberFromIndex)
                episodesToRemember.addAll(episodesToDelete)

                val keysToDelete = episodesRemoteKeysDao.getKeysFromId(rememberFromIndex)
                keysToRemember.addAll(keysToDelete)

                val episodesToHide = episodesToDelete.map { it.copy(id = -it.id) }
                episodesDao.insertEpisodes(episodesToHide)

                val keysToHide = keysToDelete.map { it.copy(id = -it.id) }
                episodesRemoteKeysDao.insertKeys(keysToHide)

                episodesDao.deleteEpisodesFromId(rememberFromIndex)
                episodesRemoteKeysDao.deleteKeysFromId(rememberFromIndex)
            }
        }

        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
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
            val episodesApiResponse = api.getPagedEpisodes(page = page)
            val episodesFromApi = episodesApiResponse.results

            val isEndOfList = episodesFromApi.isEmpty()
                    || episodesApiResponse.info?.next.isNullOrBlank()
                    || episodesApiResponse.toString().contains("error")


            database.withTransaction {
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = episodesFromApi.map {
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

                if (isEndOfList) {
                    episodesRemoteKeysDao.insertKeys(hiddenKeys)
                    episodesDao.insertEpisodes(hiddenEpisodes)

                    episodesDao.clearHiddenEpisodes()
                    episodesRemoteKeysDao.clearHiddenKeys()
                }
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            database.withTransaction {
                episodesDao.insertEpisodes(episodesToRemember)
                episodesRemoteKeysDao.insertKeys(keysToRemember)

                episodesDao.insertEpisodes(hiddenEpisodes.map { it.copy(id = -it.id) })
                episodesRemoteKeysDao.insertKeys(hiddenKeys.map { it.copy(id = -it.id) })

                episodesDao.clearHiddenEpisodes()
                episodesRemoteKeysDao.clearHiddenKeys()
            }

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

    private suspend fun getFirstRemoteKey(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { episode ->
                episodesRemoteKeysDao.remoteKeysEpisodesId(episode.id)
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