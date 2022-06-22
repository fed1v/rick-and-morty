package com.example.rickandmorty.presentation.ui.episodes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesUseCase
import com.example.rickandmorty.util.status.Resource

class EpisodesViewModel(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase
) : ViewModel() {

    fun getEpisodes() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getEpisodesUseCase.execute()))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getEpisodesByFilters(filters: EpisodeFilter) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getEpisodesByFiltersUseCase.execute(filters)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = "Nothing found"))
        }
    }
}