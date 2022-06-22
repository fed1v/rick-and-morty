package com.example.rickandmorty.presentation.ui.episodes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesUseCase

class EpisodesViewModelFactory(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EpisodesViewModel(
            getEpisodesUseCase = getEpisodesUseCase,
            getEpisodesByFiltersUseCase = getEpisodesByFiltersUseCase
        ) as T
    }
}