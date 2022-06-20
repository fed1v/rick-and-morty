package com.example.rickandmorty.domain.usecases.locations

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationByIdUseCase (
    private val repository: LocationsRepository
) {

    suspend fun execute(id: Int): LiveData<Location> {
        return repository.getLocationById(id)
    }
}