package com.example.rickandmorty.domain.usecases.locations

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationByIdUseCase (
    private val repository: LocationsRepository
) {

    suspend fun execute(id: Int): Location {
        return repository.getLocationById(id)
    }
}