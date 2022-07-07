package com.example.rickandmorty.domain.usecases.characters

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharacterByIdUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(id: Int): Character {
        return repository.getCharacterById(id)
    }
}