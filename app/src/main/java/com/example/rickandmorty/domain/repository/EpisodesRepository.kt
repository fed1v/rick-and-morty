package com.example.rickandmorty.domain.repository

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter

interface EpisodesRepository {

    suspend fun getEpisodes(): LiveData<List<Episode>>

    suspend fun getEpisodeById(id: Int): LiveData<Episode>

    suspend fun getEpisodesByIds(ids: String): List<Episode>

    suspend fun getEpisodesByFilter(filter: EpisodeFilter): LiveData<List<Episode>>
}