package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersByFilterUseCase(
    private val repository: CharactersRepository
){

    suspend fun execute(filter: CharacterFilter): List<Character> {
        return repository.getCharactersByFilter(filter)
    }
}