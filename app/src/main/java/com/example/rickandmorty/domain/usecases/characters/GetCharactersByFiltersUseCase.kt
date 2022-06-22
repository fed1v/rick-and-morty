package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersByFiltersUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(filters: CharacterFilter): List<Character> {
        return repository.getCharactersByFilters(filters)
    }
}