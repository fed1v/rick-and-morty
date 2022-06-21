package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(): List<Character>{
        return repository.getCharacters()
    }
}