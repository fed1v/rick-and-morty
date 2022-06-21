package com.example.rickandmorty.presentation.ui.characters.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.characters.GetCharacterByIdUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase

class CharacterDetailsViewModelFactory(
    private val getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase,
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterDetailsViewModel(
            getEpisodesByIdsUseCase = getEpisodesByIdsUseCase,
            getCharacterByIdUseCase = getCharacterByIdUseCase
        ) as T
    }
}