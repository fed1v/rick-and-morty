package com.example.rickandmorty.presentation.ui.characters.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase
import com.example.rickandmorty.util.status.Resource

class CharacterDetailsViewModel(
    private val getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase
) : ViewModel() {

    fun getEpisodesByIds(ids: List<Int?>) = liveData {
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
}