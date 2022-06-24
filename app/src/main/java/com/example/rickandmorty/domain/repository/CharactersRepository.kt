package com.example.rickandmorty.domain.repository

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter

interface CharactersRepository {

    suspend fun getCharacters(): List<Character>

    suspend fun getCharacterById(id: Int): Character

    suspend fun getCharactersByIds(ids: String): List<Character>

    suspend fun getCharactersByFilters(filters: CharacterFilter): List<Character>

    suspend fun getFilters(filterName: String): List<String>
}