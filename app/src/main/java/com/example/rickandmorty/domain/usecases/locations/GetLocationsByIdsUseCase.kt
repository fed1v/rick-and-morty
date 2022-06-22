package com.example.rickandmorty.domain.usecases.locations

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsByIdsUseCase(
    private val repository: LocationsRepository
) {

    suspend fun execute(ids: String): List<Location>{
        return repository.getLocationsByIds(ids)
    }
}