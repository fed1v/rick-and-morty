package com.example.rickandmorty.presentation.ui.characters.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByFiltersUseCase
import com.example.rickandmorty.domain.usecases.characters.GetCharactersFiltersUseCase
import com.example.rickandmorty.domain.usecases.characters.GetCharactersUseCase

class CharactersViewModelFactory(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase,
    private val getCharactersFiltersUseCase: GetCharactersFiltersUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharactersViewModel(
            getCharactersUseCase = getCharactersUseCase,
            getCharactersByFiltersUseCase = getCharactersByFiltersUseCase,
            getCharactersFiltersUseCase = getCharactersFiltersUseCase
        ) as T
    }
}