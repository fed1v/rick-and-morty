package com.example.rickandmorty.domain.usecases.locations

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsUseCase (
    private val repository: LocationsRepository
) {

    suspend fun execute(): List<Location> {
        return repository.getLocations()
    }
}