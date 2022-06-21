package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersByIdsUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(ids: String): List<Character>{
        return repository.getCharactersByIds(ids)
    }
}