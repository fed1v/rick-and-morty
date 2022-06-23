package com.example.rickandmorty.domain.usecases.episodes

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesByFiltersUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(filter: EpisodeFilter): List<Episode> {
        return repository.getEpisodesByFilters(filter)
    }
}