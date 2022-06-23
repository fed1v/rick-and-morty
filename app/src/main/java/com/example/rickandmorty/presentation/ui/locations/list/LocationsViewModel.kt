package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.usecases.locations.GetLocationsByFiltersUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsUseCase
import com.example.rickandmorty.util.status.Resource
import kotlinx.coroutines.Dispatchers

class LocationsViewModel(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getLocationsByFiltersUseCase: GetLocationsByFiltersUseCase
) : ViewModel() {

    fun getLocations() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getLocationsUseCase.execute()))
        } catch (e: Exception) {
            e.printStackTrace()
            println("Erroe in GetLocationsVM")
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getLocationsByFilters(filters: LocationFilter) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getLocationsByFiltersUseCase.execute(filters)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = "Nothing found"))
        }
    }
}