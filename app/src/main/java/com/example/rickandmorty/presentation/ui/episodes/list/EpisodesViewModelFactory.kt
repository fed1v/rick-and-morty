package com.example.rickandmorty.presentation.ui.episodes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.episodes.*

class EpisodesViewModelFactory(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase,
    private val getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase,
    private val getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase,
    private val getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EpisodesViewModel(
            getEpisodesUseCase = getEpisodesUseCase,
            getEpisodesByFiltersUseCase = getEpisodesByFiltersUseCase,
            getEpisodesFiltersUseCase = getEpisodesFiltersUseCase,
            getEpisodesWithPaginationUseCase = getEpisodesWithPaginationUseCase,
            getEpisodesByFiltersWithPaginationUseCase = getEpisodesByFiltersWithPaginationUseCase
        ) as T
    }
}