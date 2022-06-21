package com.example.rickandmorty.presentation.ui.characters.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase

class CharacterDetailsViewModelFactory(
    private val getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterDetailsViewModel(getEpisodesByIdsUseCase) as T
    }
}