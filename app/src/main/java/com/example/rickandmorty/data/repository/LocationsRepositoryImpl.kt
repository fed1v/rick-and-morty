package com.example.rickandmorty.data.repository

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository

class LocationsRepositoryImpl: LocationsRepository {

    override suspend fun getLocations(): List<Location> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationById(id: Int): Location {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationsByIds(ids: String): List<Location> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationsByFilter(filter: LocationFilter): List<Location> {
        TODO("Not yet implemented")
    }
}