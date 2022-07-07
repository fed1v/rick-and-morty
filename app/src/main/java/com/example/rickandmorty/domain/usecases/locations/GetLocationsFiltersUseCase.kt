package com.example.rickandmorty.domain.usecases.locations

import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsFiltersUseCase(
    private val repository: LocationsRepository
) {

    suspend fun execute(filterName: String): List<String> {
        return repository.getFilters(filterName)
    }
}