package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.locations.GetLocationsByFiltersUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsFiltersUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsUseCase

class LocationsViewModelFactory(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getLocationsByFiltersUseCase: GetLocationsByFiltersUseCase,
    private val getLocationsFiltersUseCase: GetLocationsFiltersUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationsViewModel(
            getLocationsUseCase = getLocationsUseCase,
            getLocationsByFiltersUseCase = getLocationsByFiltersUseCase,
            getLocationsFiltersUseCase = getLocationsFiltersUseCase
        ) as T
    }
}