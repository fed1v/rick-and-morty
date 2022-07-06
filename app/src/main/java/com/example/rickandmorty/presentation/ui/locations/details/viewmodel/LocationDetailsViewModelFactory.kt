package com.example.rickandmorty.presentation.ui.locations.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase

class LocationDetailsViewModelFactory(
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val getCharactersByIdsUseCase: GetCharactersByIdsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationDetailsViewModel(
            getLocationByIdUseCase = getLocationByIdUseCase,
            getCharactersByIdsUseCase = getCharactersByIdsUseCase
        ) as T
    }
}