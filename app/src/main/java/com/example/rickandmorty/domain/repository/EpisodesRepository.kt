package com.example.rickandmorty.domain.repository

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter

interface EpisodesRepository {

    suspend fun getEpisodes(): List<Episode>

    suspend fun getEpisodeById(id: Int): Episode

    suspend fun getEpisodesByIds(ids: String): List<Episode>

    suspend fun getEpisodesByFilters(filters: EpisodeFilter): List<Episode>

    suspend fun getFilters(filterName: String): List<String>
}