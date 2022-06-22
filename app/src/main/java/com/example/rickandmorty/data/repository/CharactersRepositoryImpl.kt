package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.mapper.CharacterDataToCharacterDomainMapper
import com.example.rickandmorty.data.mapper.CharacterDtoToCharacterEntityMapper
import com.example.rickandmorty.data.mapper.CharacterEntityToCharacterDomainMapper
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl(
    private val api: CharactersApi,
    private val dao: CharactersDao
) : CharactersRepository {

    private val mapper = CharacterDataToCharacterDomainMapper()
    private val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()
    private val mapperEntityToModel = CharacterEntityToCharacterDomainMapper()

    override suspend fun getCharacters(): List<Character> {
        try {
            val charactersFromApi = api.getCharacters().results
            val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertCharacters(charactersEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val charactersFromDB = dao.getCharacters()

        return charactersFromDB.map { mapperEntityToModel.map(it) }
    }

    override suspend fun getCharacterById(id: Int): Character {
        try {
            val characterFromApi = api.getCharacterById(id)
            val characterEntity = mapperDtoToEntity.map(characterFromApi)
            dao.insertCharacter(characterEntity)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val characterFromDB = dao.getCharacterById(id)

        return mapperEntityToModel.map(characterFromDB)
    }

    override suspend fun getCharactersByIds(ids: String): List<Character> {
        return api.getCharactersByIds(ids).map { mapper.map(it) }
    }

    override suspend fun getCharactersByFilters(filters: CharacterFilter): List<Character> {
        val filtersToApply = mapOf(
            "name" to filters.name,
            "status" to filters.status,
            "species" to filters.species,
            "type" to filters.type,
            "gender" to filters.gender,
        ).filter { it.value != null }

        return api.getCharactersByFilters(filtersToApply).results.map { mapper.map(it) }
    }
}