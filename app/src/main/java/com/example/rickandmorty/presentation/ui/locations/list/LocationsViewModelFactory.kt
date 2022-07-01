package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.locations.*

class LocationsViewModelFactory(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getLocationsByFiltersUseCase: GetLocationsByFiltersUseCase,
    private val getLocationsFiltersUseCase: GetLocationsFiltersUseCase,
    private val getLocationsWithPaginationUseCase: GetLocationsWithPaginationUseCase,
    private val getLocationsByFiltersWithPaginationUseCase: GetLocationsByFiltersWithPaginationUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationsViewModel(
            getLocationsUseCase = getLocationsUseCase,
            getLocationsByFiltersUseCase = getLocationsByFiltersUseCase,
            getLocationsFiltersUseCase = getLocationsFiltersUseCase,
            getLocationsWithPaginationUseCase = getLocationsWithPaginationUseCase,
            getLocationsByFiltersWithPaginationUseCase = getLocationsByFiltersWithPaginationUseCase
        ) as T
    }
}