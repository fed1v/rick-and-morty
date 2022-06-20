package com.example.rickandmorty.domain.usecases.characters

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.repository.CharactersRepository

class GetCharactersUseCase(
    private val repository: CharactersRepository
) {

    suspend fun execute(): LiveData<List<Character>>{
        return repository.getCharacters()
    }
}