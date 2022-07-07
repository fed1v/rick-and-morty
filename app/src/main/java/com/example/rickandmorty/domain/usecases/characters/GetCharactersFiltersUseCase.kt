package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersFiltersUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(filterName: String): List<String> {
        return repository.getFilters(filterName)
    }
}