package com.example.rickandmorty.domain.usecases.episodes

import com.example.rickandmorty.domain.repository.EpisodesRepository

class GetEpisodesFiltersUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(filterName: String): List<String> {
        return repository.getFilters(filterName)
    }
}