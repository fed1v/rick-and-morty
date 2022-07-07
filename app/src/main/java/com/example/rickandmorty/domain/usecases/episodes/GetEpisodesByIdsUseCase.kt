package com.example.rickandmorty.domain.usecases.episodes

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesByIdsUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(ids: String): List<Episode> {
        return repository.getEpisodesByIds(ids)
    }
}