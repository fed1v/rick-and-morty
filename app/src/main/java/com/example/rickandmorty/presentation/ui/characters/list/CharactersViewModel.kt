package com.example.rickandmorty.presentation.ui.characters.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.usecases.characters.*
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.util.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CharactersViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase,
    private val getCharactersFiltersUseCase: GetCharactersFiltersUseCase,
    private val getCharactersWithPaginationUseCase: GetCharactersWithPaginationUseCase,
    private val getCharactersByFiltersWithPaginationUseCase: GetCharactersByFiltersWithPaginationUseCase
) : ViewModel() {

    private var _charactersFlow = MutableSharedFlow<PagingData<CharacterPresentation>>()
    val charactersFlow: SharedFlow<PagingData<CharacterPresentation>> = _charactersFlow

    fun getFilters() = liveData<Pair<String, List<String>>>(Dispatchers.IO) {
        emit(Pair("species", getCharactersFiltersUseCase.execute("species")))
        emit(Pair("type", getCharactersFiltersUseCase.execute("type")))
    }

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


    suspend fun getCharactersWithPagination() {
        val mapperDomainToPresentation = CharacterDomainToCharacterPresentationMapper()

        getCharactersWithPaginationUseCase.execute()
            .onEach { data ->
                _charactersFlow.emit(
                    data.map { character -> mapperDomainToPresentation.map(character) }
                )
            }.launchIn(viewModelScope)
    }

    suspend fun getCharactersByFiltersWithPagination(filters: CharacterFilter) {
        val mapperDomainToPresentation = CharacterDomainToCharacterPresentationMapper()

        getCharactersByFiltersWithPaginationUseCase.execute(filters)
            .onEach { data ->
                _charactersFlow.emit(
                    data.map { character -> mapperDomainToPresentation.map(character) }
                )
            }.launchIn(viewModelScope)
    }
}