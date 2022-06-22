package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.mapper.EpisodeDataToEpisodeDomainMapper
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class EpisodesRepositoryImpl(
    private val api: EpisodesApi
) : EpisodesRepository {

    private val mapper = EpisodeDataToEpisodeDomainMapper()

    override suspend fun getEpisodes(): List<Episode> {
        return api.getEpisodes().results.map { mapper.map(it) }
    }

    override suspend fun getEpisodeById(id: Int): Episode {
        return mapper.map(api.getEpisodeById(id))
    }

    override suspend fun getEpisodesByIds(ids: String): List<Episode> {
        return api.getEpisodesByIds(ids).map { mapper.map(it) }
    }

    override suspend fun getEpisodesByFilters(filters: EpisodeFilter): List<Episode> {
        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "episode" to filters.episode
        ).filter { it.value != null }

        return api.getEpisodesByFilters(filtersToApply).results.map { mapper.map(it) }
    }
}