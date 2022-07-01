package com.example.rickandmorty.domain.usecases.locations

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository
import kotlinx.coroutines.flow.Flow

class GetLocationsByFiltersWithPaginationUseCase(
    private val repository: LocationsRepository
) {

    suspend fun execute(filters: LocationFilter): Flow<PagingData<Location>> {
        return repository.getLocationsByFiltersWithPagination(filters)
    }
}