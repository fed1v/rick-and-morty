package com.example.rickandmorty.domain.repository

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter

interface LocationsRepository {

    suspend fun getLocations(): List<Location>

    suspend fun getLocationById(id: Int): Location

    suspend fun getLocationsByIds(ids: String): List<Location>

    suspend fun getLocationsByFilters(filters: LocationFilter): List<Location>

    suspend fun getFilters(filterName: String): List<String>
}