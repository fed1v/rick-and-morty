package com.example.rickandmorty.domain.usecases.characters

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersByFilterUseCase(
    private val repository: CharactersRepository
){

    suspend fun execute(filter: CharacterFilter): LiveData<List<Character>> {
        return repository.getCharactersByFilter(filter)
    }
}