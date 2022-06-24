package com.example.rickandmorty.presentation.ui.characters.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.usecases.characters.GetCharacterByIdUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.resource.Resource
import kotlinx.coroutines.Dispatchers

class CharacterDetailsViewModel(
    private val getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase,
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase
) : ViewModel() {

    fun getEpisodesByIds(ids: List<Int?>) = liveData(Dispatchers.IO) {
        if (ids.isEmpty()) {
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
            emit(Resource.success(data = getEpisodesByIdsUseCase.execute(idsString)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
            e.printStackTrace()
        }
    }

    fun getCharacterById(id: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = getCharacterByIdUseCase.execute(id)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getLocation(location: LocationPresentation) = liveData(Dispatchers.IO) {
        if (location.id == -1) {
            emit(
                Resource.success(
                    Location(
                        id = -1,
                        name = "unknown_location",
                        type = "",
                        dimension = "",
                        residents = emptyList()
                    )
                )
            )
            return@liveData
        }

        emit(Resource.loading(null))
        try {
            val result = getLocationByIdUseCase.execute(location.id)
            if (result.name == "unknown") {
                emit(Resource.success(data = null))
            } else {
                emit(Resource.success(data = result))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getOrigin(origin: LocationPresentation) = liveData(Dispatchers.IO) {
        if (origin.id == -1) {
            emit(
                Resource.success(
                    Location(
                        id = -1,
                        name = "unknown_origin",
                        type = "",
                        dimension = "",
                        residents = emptyList()
                    )
                )
            )
            return@liveData
        }

        emit(Resource.loading(null))
        try {
            val result = getLocationByIdUseCase.execute(origin.id)
            if (result.name == "unknown") {
                emit(Resource.success(data = null))
            } else {
                emit(Resource.success(data = result))
            }
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

}