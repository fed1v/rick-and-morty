package com.example.rickandmorty.domain.usecases.episodes

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(): LiveData<List<Episode>> {
        return repository.getEpisodes()
    }
}