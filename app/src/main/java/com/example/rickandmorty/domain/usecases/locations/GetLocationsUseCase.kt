package com.example.rickandmorty.domain.usecases.locations

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsUseCase (
    private val repository: LocationsRepository
) {

    suspend fun execute(): LiveData<List<Location>> {
        return repository.getLocations()
    }
}