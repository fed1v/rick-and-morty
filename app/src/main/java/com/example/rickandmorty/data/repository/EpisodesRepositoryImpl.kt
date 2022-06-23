package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.mapper.EpisodeDtoToEpisodeDomainMapper
import com.example.rickandmorty.data.mapper.EpisodeDtoToEpisodeEntityMapper
import com.example.rickandmorty.data.mapper.EpisodeEntityToEpisodeDomainMapper
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class EpisodesRepositoryImpl(
    private val api: EpisodesApi,
    private val dao: EpisodesDao
) : EpisodesRepository {

    private val mapperDtoToDomain = EpisodeDtoToEpisodeDomainMapper()
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

        return api.getEpisodesByFilters(filtersToApply).results.map { mapperDtoToDomain.map(it) }
    }
}