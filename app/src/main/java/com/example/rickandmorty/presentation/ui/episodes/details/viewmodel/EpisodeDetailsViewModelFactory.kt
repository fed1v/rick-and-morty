package com.example.rickandmorty.presentation.ui.episodes.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodeByIdUseCase

class EpisodeDetailsViewModelFactory(
    private val getEpisodeByIdUseCase: GetEpisodeByIdUseCase,
    private val getCharactersByIdsUseCase: GetCharactersByIdsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EpisodeDetailsViewModel(
            getEpisodeByIdUseCase = getEpisodeByIdUseCase,
            getCharactersByIdsUseCase = getCharactersByIdsUseCase
        ) as T
    }
}