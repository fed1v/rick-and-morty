package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.locations.GetLocationsUseCase

class LocationsViewModelFactory(
    private val getLocationsUseCase: GetLocationsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationsViewModel(getLocationsUseCase) as T
    }
}