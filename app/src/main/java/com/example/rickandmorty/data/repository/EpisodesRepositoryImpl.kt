package com.example.rickandmorty.data.repository

import androidx.lifecycle.LiveData
import com.example.rickandmorty.data.mapper.EpisodeDataToEpisodeModelMapper
import com.example.rickandmorty.data.remote.EpisodesApi
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class EpisodesRepositoryImpl(
    private val api: EpisodesApi
) : EpisodesRepository {

    override suspend fun getEpisodes(): LiveData<List<Episode>> {
        TODO("Not yet implemented")
    }

    override suspend fun getEpisodeById(id: Int): LiveData<Episode> {
        TODO("Not yet implemented")
    }

    override suspend fun getEpisodesByIds(ids: String): List<Episode> {
        val mapper = EpisodeDataToEpisodeModelMapper()
        val result = api.getEpisodesByIds(ids).map { mapper.map(it) }
        println("Results Repository: ${result}")
        return result//api.getEpisodesByIds(ids).results?.map { mapper.map(it) } ?: listOf()

    }

    override suspend fun getEpisodesByFilter(filter: EpisodeFilter): LiveData<List<Episode>> {
        TODO("Not yet implemented")
    }
}