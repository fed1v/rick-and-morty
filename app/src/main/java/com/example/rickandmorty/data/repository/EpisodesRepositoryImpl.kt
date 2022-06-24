package com.example.rickandmorty.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.mapper.episode.EpisodeDtoToEpisodeEntityMapper
import com.example.rickandmorty.data.mapper.episode.EpisodeEntityToEpisodeDomainMapper
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class EpisodesRepositoryImpl(
    private val api: EpisodesApi,
    private val dao: EpisodesDao
) : EpisodesRepository {

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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val idsList = IdsConverter().fromStringIds(ids)
        val episodesFromDB = dao.getEpisodesByIds(idsList)

        return episodesFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getEpisodesByFilters(filters: EpisodeFilter): List<Episode> {
        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "episode" to filters.episode
        ).filter { it.value != null }

        try {
            val episodesFromApi = api.getEpisodesByFilters(filtersToApply)
            val episodesEntities = episodesFromApi.results.map { mapperDtoToEntity.map(it) }
            dao.insertEpisodes(episodesEntities)
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
}