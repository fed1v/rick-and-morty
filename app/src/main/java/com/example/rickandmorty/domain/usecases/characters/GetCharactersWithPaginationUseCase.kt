package com.example.rickandmorty.domain.usecases.characters

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersWithPaginationUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(): Flow<PagingData<Character>> {
        return repository.getCharactersWithPagination()
    }
}