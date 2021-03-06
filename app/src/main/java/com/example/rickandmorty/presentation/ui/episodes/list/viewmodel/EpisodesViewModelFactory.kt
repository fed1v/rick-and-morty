package com.example.rickandmorty.presentation.ui.episodes.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.episodes.*

class EpisodesViewModelFactory(
    private val getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase,
    private val getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase,
    private val getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EpisodesViewModel(
            getEpisodesFiltersUseCase = getEpisodesFiltersUseCase,
            getEpisodesWithPaginationUseCase = getEpisodesWithPaginationUseCase,
            getEpisodesByFiltersWithPaginationUseCase = getEpisodesByFiltersWithPaginationUseCase
        ) as T
    }
}