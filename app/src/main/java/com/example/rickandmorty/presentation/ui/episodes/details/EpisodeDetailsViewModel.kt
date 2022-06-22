package com.example.rickandmorty.presentation.ui.episodes.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodeByIdUseCase
import com.example.rickandmorty.util.status.Resource

class EpisodeDetailsViewModel(
    private val getEpisodeByIdUseCase: GetEpisodeByIdUseCase,
    private val getCharactersByIdsUseCase: GetCharactersByIdsUseCase
) : ViewModel() {

    fun getEpisode(id: Int) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getEpisodeByIdUseCase.execute(id)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getEpisodeCharactersByIds(ids: List<Int?>) = liveData {
        if(ids.isEmpty()) {
            emit(Resource.loading(data = null))
            emit(Resource.success(listOf()))
            return@liveData
        }

        val idsString = ids.joinToString(",")
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getCharactersByIdsUseCase.execute(idsString)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }
}