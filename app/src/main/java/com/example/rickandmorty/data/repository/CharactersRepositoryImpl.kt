package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.mapper.CharacterDataToCharacterDomainMapper
import com.example.rickandmorty.data.remote.CharactersApi
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl(
    private val api: CharactersApi
) : CharactersRepository {

    private val mapper = CharacterDataToCharacterDomainMapper()

    override suspend fun getCharacters(): List<Character> {
        return api.getCharacters().results.map { mapper.map(it) }
    }

    override suspend fun getCharacterById(id: Int): Character {
        return mapper.map(api.getCharacterById(id))
    }

    override suspend fun getCharactersByIds(ids: String): List<Character> {
        return api.getCharactersByIds(ids).map { mapper.map(it) }
    }

    override suspend fun getCharactersByFilter(filter: CharacterFilter): List<Character> {
        TODO("Not yet implemented")
    }
}