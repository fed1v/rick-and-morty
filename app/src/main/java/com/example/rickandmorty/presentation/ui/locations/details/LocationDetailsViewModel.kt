package com.example.rickandmorty.presentation.ui.locations.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.util.status.Resource

class LocationDetailsViewModel(
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val getCharactersByIdsUseCase: GetCharactersByIdsUseCase
) : ViewModel() {

    fun getLocation(id: Int) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getLocationByIdUseCase.execute(id)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getResidents(ids: List<Int?>) = liveData {
        if(ids.isEmpty()) {
            emit(Resource.loading(data = null))
            emit(Resource.success(listOf()))
            return@liveData
        }

        var idsString = ""
        if (ids.size == 1) {
            idsString = "${ids[0]},${ids[0]}"
        } else {
            idsString = ids.joinToString(",")
        }

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getCharactersByIdsUseCase.execute(idsString)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

}