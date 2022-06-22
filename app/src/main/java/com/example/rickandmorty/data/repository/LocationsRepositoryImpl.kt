package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.mapper.LocationDataToLocationDomainMapper
import com.example.rickandmorty.data.remote.locations.LocationsApi
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository

class LocationsRepositoryImpl(
    private val api: LocationsApi
) : LocationsRepository {

    private val mapper = LocationDataToLocationDomainMapper()

    override suspend fun getLocations(): List<Location> {
        return api.getLocations().results.map { mapper.map(it) }
    }

    override suspend fun getLocationById(id: Int): Location {
        return mapper.map(api.getLocationById(id))
    }

    override suspend fun getLocationsByIds(ids: String): List<Location> {
        return api.getLocationsByIds(ids).map { mapper.map(it) }
    }

    override suspend fun getLocationsByFilter(filter: LocationFilter): List<Location> {
        TODO("Not yet implemented")
    }
}