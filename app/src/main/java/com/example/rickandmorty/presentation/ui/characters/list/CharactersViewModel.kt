package com.example.rickandmorty.presentation.ui.characters.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByFiltersUseCase
import com.example.rickandmorty.domain.usecases.characters.GetCharactersUseCase
import com.example.rickandmorty.util.status.Resource
import kotlinx.coroutines.Dispatchers

class CharactersViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase
) : ViewModel() {

    fun getCharacters() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = getCharactersUseCase.execute()))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error"))
        }
    }

    fun getCharactersByFilters(filters: CharacterFilter) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = getCharactersByFiltersUseCase.execute(filters)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = "Nothing found"))
        }
    }

}