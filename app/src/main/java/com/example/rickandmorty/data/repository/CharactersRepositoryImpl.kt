package com.example.rickandmorty.data.repository

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl : CharactersRepository {

    override suspend fun getCharacters(): LiveData<List<Character>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCharacterById(id: Int): LiveData<Character> {
        TODO("Not yet implemented")
    }

    override suspend fun getCharactersByFilter(filter: CharacterFilter): LiveData<List<Character>> {
        TODO("Not yet implemented")
    }
}