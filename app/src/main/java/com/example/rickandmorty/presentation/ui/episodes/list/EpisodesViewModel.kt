package com.example.rickandmorty.presentation.ui.episodes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.usecases.episodes.*
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationMapper
import com.example.rickandmorty.presentation.models.EpisodePresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class EpisodesViewModel(
    private val getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase,
    private val getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase,
    private val getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase
) : ViewModel() {

    private var _episodesFlow = MutableSharedFlow<PagingData<EpisodePresentation>>()
    val episodesFlow: SharedFlow<PagingData<EpisodePresentation>> = _episodesFlow

    fun getFilters() = liveData<Pair<String, List<String>>>(Dispatchers.IO) {
        emit(Pair("episode", getEpisodesFiltersUseCase.execute("episode")))
    }

    suspend fun getEpisodesWithPagination() {
        val mapperDomainToPresentation = EpisodeDomainToEpisodePresentationMapper()

        getEpisodesWithPaginationUseCase.execute()
            .onEach { data ->
                _episodesFlow.emit(
                    data.map { episode -> mapperDomainToPresentation.map(episode) }
                )
            }.launchIn(viewModelScope)
    }

    suspend fun getEpisodesByFiltersWithPagination(filters: EpisodeFilter) {
        val mapperDomainToPresentation = EpisodeDomainToEpisodePresentationMapper()

        getEpisodesByFiltersWithPaginationUseCase.execute(filters)
            .onEach { data ->
                _episodesFlow.emit(
                    data.map { episode -> mapperDomainToPresentation.map(episode) }
                )
            }.launchIn(viewModelScope)
    }
}