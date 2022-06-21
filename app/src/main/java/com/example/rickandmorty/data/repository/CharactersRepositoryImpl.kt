package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.mapper.CharacterDataToCharacterDomainModelMapper
import com.example.rickandmorty.data.remote.CharactersApi
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl(
    private val api: CharactersApi
) : CharactersRepository {

    override suspend fun getCharacters(): List<Character> {
        val mapperDataToDomain = CharacterDataToCharacterDomainModelMapper()
        return api.getCharacters().results.map { mapperDataToDomain.map(it) }
    }

    override suspend fun getCharacterById(id: Int): Character {
        val mapperDataToDomain = CharacterDataToCharacterDomainModelMapper()
        return mapperDataToDomain.map(api.getCharacterById(id))
    }

    override suspend fun getCharactersByIds(ids: String): List<Character> {
        val mapperDataToDomain = CharacterDataToCharacterDomainModelMapper()
        return api.getCharactersByIds(ids).map { mapperDataToDomain.map(it) }
    }

    override suspend fun getCharactersByFilter(filter: CharacterFilter): List<Character> {
        TODO("Not yet implemented")
    }
}