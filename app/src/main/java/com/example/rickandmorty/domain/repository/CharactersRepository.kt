package com.example.rickandmorty.domain.repository

import androidx.lifecycle.LiveData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter

interface CharactersRepository {

    suspend fun getCharacters(): List<Character>

    suspend fun getCharacterById(id: Int): LiveData<Character>

    suspend fun getCharactersByFilter(filter: CharacterFilter): LiveData<List<Character>>
}