package com.example.rickandmorty.data.repository

import androidx.paging.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.local.database.episodes.remote_keys.EpisodeRemoteKeys
import com.example.rickandmorty.data.mapper.episode.EpisodeDtoToEpisodeEntityMapper
import com.example.rickandmorty.data.mapper.episode.EpisodeEntityToEpisodeDomainMapper
import com.example.rickandmorty.data.pagination.episodes.EpisodesFiltersMediator
import com.example.rickandmorty.data.pagination.episodes.EpisodesMediator
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class EpisodesRepositoryImpl(
    private val api: EpisodesApi,
    private val database: RickAndMortyDatabase
) : EpisodesRepository {

    private val dao = database.episodesDao
    private val keysDao = database.episodesRemoteKeysDao // insert keys

    private val mapperDtoToEntity = EpisodeDtoToEpisodeEntityMapper()
    private val mapperEntityToDomain = EpisodeEntityToEpisodeDomainMapper()

    override suspend fun getEpisodes(): List<Episode> {
        try {
            val episodesFromApi = api.getEpisodes().results
            val episodesEntities = episodesFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertEpisodes(episodesEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val episodesFromDB = dao.getEpisodes()

        return episodesFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getEpisodeById(id: Int): Episode {
        try {
            val episodeFromApi = api.getEpisodeById(id)
            val episodeEntity = mapperDtoToEntity.map(episodeFromApi)
            dao.insertEpisode(episodeEntity)

            var prevKey: Int? = (episodeFromApi.id - 1) / 20
            if (prevKey != null && prevKey <= 0) prevKey = null
            val nextKey = (prevKey?.plus(2)) ?: 2

            val key = EpisodeRemoteKeys(
                id = episodeFromApi.id,
                prevKey = prevKey,
                nextKey = nextKey
            )

            keysDao.insertKeys(listOf(key))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val episodeFromDB = dao.getEpisodeById(id)

        return mapperEntityToDomain.map(episodeFromDB)
    }

    override suspend fun getEpisodesByIds(ids: String): List<Episode> {
        try {
            val episodesFromApi = api.getEpisodesByIds(ids)
            val episodesEntities = episodesFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertEpisodes(episodesEntities)

            val keys = episodesFromApi.map {
                var prevKey: Int? = (it.id - 1) / 20
                if (prevKey != null && prevKey <= 0) prevKey = null
                val nextKey = (prevKey?.plus(2)) ?: 2

                EpisodeRemoteKeys(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            keysDao.insertKeys(keys)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val idsList = IdsConverter().fromStringIds(ids)
        val episodesFromDB = dao.getEpisodesByIds(idsList)

        return episodesFromDB.map { mapperEntityToDomain.map(it) }
            .distinctBy { it.id }
            .sortedBy { it.id }
    }

    override suspend fun getEpisodesByFilters(filters: EpisodeFilter): List<Episode> {
        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "episode" to filters.episode
        ).filter { it.value != null }

        try {
            val episodesFromApi = api.getEpisodesByFilters(filtersToApply).results
            val episodesEntities = episodesFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertEpisodes(episodesEntities)

            val keys = episodesFromApi.map {
                var prevKey: Int? = (it.id - 1) / 20
                if (prevKey != null && prevKey <= 0) prevKey = null
                val nextKey = (prevKey?.plus(2)) ?: 2

                EpisodeRemoteKeys(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            keysDao.insertKeys(keys)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val episodesFromDB = dao.getEpisodesByFilters(
            name = filtersToApply["name"],
            episode = filtersToApply["episode"]
        )

        return episodesFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getFilters(filterName: String): List<String> {
        val query = SimpleSQLiteQuery("SELECT DISTINCT $filterName FROM episodes")
        return dao.getFilters(query)
    }

    override suspend fun getEpisodesWithPagination(): Flow<PagingData<Episode>> {
        val pagingSourceFactory = {
            dao.getPagedEpisodesFromId(0)
        }

        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = EpisodesMediator(
                api = api,
                database = database
            )
        ).flow.map { data ->
            data.map { episodeEntity ->
                mapperEntityToDomain.map(episodeEntity)
            }
        }
    }

    override suspend fun getEpisodesByFiltersWithPagination(filters: EpisodeFilter): Flow<PagingData<Episode>> {
        val filtersToApply = mapOf(
            "name" to filters.name,
            "episode" to filters.episode
        ).filter { it.value != null }


        val pagingSourceFactory = {
            dao.getPagedEpisodesByFilters(
                name = filtersToApply["name"],
                episode = filtersToApply["episode"]
            )
        }

        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = EpisodesFiltersMediator(
                api = api,
                database = database,
                episodeFilters = filters
            )
        ).flow.map { data ->
            data.map { episode ->
                mapperEntityToDomain.map(episode)
            }
        }
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            prefetchDistance = 0,
            initialLoadSize = 2,
            maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
            jumpThreshold = Int.MIN_VALUE
        )
    }
}