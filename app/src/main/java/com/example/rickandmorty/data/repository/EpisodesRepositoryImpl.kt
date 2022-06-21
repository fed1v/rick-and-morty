package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.mapper.EpisodeDataToEpisodeModelMapper
import com.example.rickandmorty.data.remote.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class EpisodesRepositoryImpl(
    private val api: EpisodesApi
) : EpisodesRepository {

    override suspend fun getEpisodes(): List<Episode> {
        val mapper = EpisodeDataToEpisodeModelMapper()
        return api.getEpisodes().results.map { mapper.map(it) }
    }

    override suspend fun getEpisodeById(id: Int): Episode {
        val mapper = EpisodeDataToEpisodeModelMapper()
        return mapper.map(api.getEpisodeById(id))
    }

    override suspend fun getEpisodesByIds(ids: String): List<Episode> {
        val mapper = EpisodeDataToEpisodeModelMapper()
        return api.getEpisodesByIds(ids).map { mapper.map(it) }

    }

    override suspend fun getEpisodesByFilter(filter: EpisodeFilter): List<Episode> {
        TODO("Not yet implemented")
    }
}