package com.example.rickandmorty.domain.usecases.episodes

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodeByIdUseCase (
    private val repository: EpisodesRepository
) {

    suspend fun execute(id: Int): Episode {
        return repository.getEpisodeById(id)
    }
}