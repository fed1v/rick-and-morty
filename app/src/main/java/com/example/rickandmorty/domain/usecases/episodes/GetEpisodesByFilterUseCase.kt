package com.example.rickandmorty.domain.usecases.episodes

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesByFilterUseCase (
    private val repository: EpisodesRepository
){

    suspend fun execute(filter: EpisodeFilter): LiveData<List<Episode>> {
        return repository.getEpisodesByFilter(filter)
    }
}