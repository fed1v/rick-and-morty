package com.example.rickandmorty.domain.usecases.episodes

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(): List<Episode> {
        return repository.getEpisodes()
    }
}