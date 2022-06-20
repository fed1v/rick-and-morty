package com.example.rickandmorty.domain.usecases.locations

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository

class GetLocationsByFilterUseCase (
    private val repository: LocationsRepository
){

    suspend fun execute(filter: LocationFilter): LiveData<List<Location>> {
        return repository.getLocationsByFilter(filter)
    }
}