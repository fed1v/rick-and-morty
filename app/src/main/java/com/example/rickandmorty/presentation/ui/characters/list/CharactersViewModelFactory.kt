package com.example.rickandmorty.presentation.ui.characters.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.characters.GetCharactersUseCase

class CharactersViewModelFactory(
    private val getCharactersUseCase: GetCharactersUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharactersViewModel(getCharactersUseCase) as T
    }
}