package com.example.rickandmorty.domain.usecases.characters

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersByFiltersWithPaginationUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(filters: CharacterFilter): Flow<PagingData<Character>> {
        return repository.getCharactersByFiltersWithPagination(filters)
    }
}