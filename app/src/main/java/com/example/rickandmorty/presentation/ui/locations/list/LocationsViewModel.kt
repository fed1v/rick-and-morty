package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.usecases.locations.GetLocationsUseCase
import com.example.rickandmorty.util.status.Resource

class LocationsViewModel(
    private val getLocationsUseCase: GetLocationsUseCase
) : ViewModel() {

    fun getLocations() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getLocationsUseCase.execute()))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }
}