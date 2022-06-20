package com.example.rickandmorty.domain.repository

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter

interface LocationsRepository {

    suspend fun getLocations(): LiveData<List<Location>>

    suspend fun getLocationById(id: Int): LiveData<Location>

    suspend fun getLocationsByFilter(filter: LocationFilter): LiveData<List<Location>>
}