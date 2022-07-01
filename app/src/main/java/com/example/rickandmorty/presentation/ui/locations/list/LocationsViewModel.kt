package com.example.rickandmorty.presentation.ui.locations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.usecases.locations.*
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationMapper
import com.example.rickandmorty.presentation.mapper.LocationDomainToLocationPresentationMapper
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationsViewModel(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getLocationsByFiltersUseCase: GetLocationsByFiltersUseCase,
    private val getLocationsFiltersUseCase: GetLocationsFiltersUseCase,
    private val getLocationsWithPaginationUseCase: GetLocationsWithPaginationUseCase,
    private val getLocationsByFiltersWithPaginationUseCase: GetLocationsByFiltersWithPaginationUseCase
) : ViewModel() {

    private var _locationsFlow = MutableSharedFlow<PagingData<LocationPresentation>>()
    val locationsFlow: SharedFlow<PagingData<LocationPresentation>> = _locationsFlow

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

    fun getFilters() = liveData<Pair<String, List<String>>>(Dispatchers.IO) {
        emit(Pair("type", getLocationsFiltersUseCase.execute("type")))
        emit(Pair("dimension", getLocationsFiltersUseCase.execute("dimension")))
    }

    suspend fun getLocationsWithPagination() {
        val mapperDomainToPresentation = LocationDomainToLocationPresentationMapper()

        getLocationsWithPaginationUseCase.execute()
            .onEach { data ->
                _locationsFlow.emit(
                    data.map { location -> mapperDomainToPresentation.map(location) }
                )
            }.launchIn(viewModelScope)
    }

    suspend fun getLocationsByFiltersWithPagination(filters: LocationFilter) {
        val mapperDomainToPresentation = LocationDomainToLocationPresentationMapper()

        getLocationsByFiltersWithPaginationUseCase.execute(filters)
            .onEach { data ->
                _locationsFlow.emit(
                    data.map { location -> mapperDomainToPresentation.map(location) }
                )
            }.launchIn(viewModelScope)
    }
}