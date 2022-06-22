package com.example.rickandmorty.domain.usecases.locations

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsByFiltersUseCase(
    private val repository: LocationsRepository
) {

    suspend fun execute(filter: LocationFilter): List<Location> {
        return repository.getLocationsByFilters(filter)
    }
}