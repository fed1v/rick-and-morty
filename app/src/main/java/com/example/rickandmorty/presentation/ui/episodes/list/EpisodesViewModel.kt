package com.example.rickandmorty.presentation.ui.episodes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesUseCase
import com.example.rickandmorty.util.resource.Resource
import kotlinx.coroutines.Dispatchers

class EpisodesViewModel(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase,
    private val getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase
) : ViewModel() {

    fun getEpisodes() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getEpisodesUseCase.execute()))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getEpisodesByFilters(filters: EpisodeFilter) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = getEpisodesByFiltersUseCase.execute(filters)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = "Nothing found"))
        }
    }

    fun getFilters() = liveData<Pair<String, List<String>>>(Dispatchers.IO) {
        emit(Pair("episode", getEpisodesFiltersUseCase.execute("episode")))
    }

}