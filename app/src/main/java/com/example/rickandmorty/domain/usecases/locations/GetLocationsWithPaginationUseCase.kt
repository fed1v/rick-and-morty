package com.example.rickandmorty.domain.usecases.locations

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.repository.LocationsRepository
import kotlinx.coroutines.flow.Flow

class GetLocationsWithPaginationUseCase(
    private val repository: LocationsRepository
) {

    suspend fun execute(): Flow<PagingData<Location>> {
        return repository.getLocationsWithPagination()
    }
}