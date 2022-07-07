package com.example.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import kotlinx.coroutines.flow.Flow

interface LocationsRepository {

    suspend fun getLocationById(id: Int): Location

    suspend fun getLocationsByIds(ids: String): List<Location>

    suspend fun getFilters(filterName: String): List<String>

    suspend fun getLocationsWithPagination(): Flow<PagingData<Location>>

    suspend fun getLocationsByFiltersWithPagination(filters: LocationFilter): Flow<PagingData<Location>>
}