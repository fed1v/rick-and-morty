package com.example.rickandmorty.data.repository

import androidx.paging.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.mapper.character.CharacterDtoToCharacterEntityMapper
import com.example.rickandmorty.data.mapper.character.CharacterEntityToCharacterDomainMapper
import com.example.rickandmorty.data.pagination.CharactersMediator
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharactersRepositoryImpl(
    private val api: CharactersApi,
    private val database: RickAndMortyDatabase
) : CharactersRepository {

    private val dao = database.charactersDao

    private val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()
    private val mapperEntityToDomain = CharacterEntityToCharacterDomainMapper()

    override suspend fun getCharacters(): List<Character> {
        try {
            val charactersFromApi = api.getCharacters().results
            val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertCharacters(charactersEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val charactersFromDB = dao.getCharacters()

        return charactersFromDB.map { mapperEntityToDomain.map(it) }
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

        return mapperEntityToDomain.map(characterFromDB)
    }

    override suspend fun getCharactersByIds(ids: String): List<Character> {
        try {
            val charactersFromApi = api.getCharactersByIds(ids)
            val charactersEntities = charactersFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertCharacters(charactersEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val idsList = IdsConverter().fromStringIds(ids)
        val charactersFromDB = dao.getCharactersByIds(idsList)

        return charactersFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getCharactersByFilters(filters: CharacterFilter): List<Character> {
        val filtersToApply = mapOf(
            "name" to filters.name,
            "status" to filters.status,
            "species" to filters.species,
            "type" to filters.type,
            "gender" to filters.gender,
        ).filter { it.value != null }

        try {
            val charactersFromApi = api.getCharactersByFilters(filtersToApply)
            val charactersEntities = charactersFromApi.results.map { mapperDtoToEntity.map(it) }
            dao.insertCharacters(charactersEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val charactersFromDB = dao.getCharactersByFilters(
            name = filtersToApply["name"],
            status = filtersToApply["status"],
            species = filtersToApply["species"],
            type = filtersToApply["type"],
            gender = filtersToApply["gender"],
        )

        return charactersFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getFilters(filterName: String): List<String> {
        val query = SimpleSQLiteQuery("SELECT DISTINCT $filterName FROM characters")
        return dao.getFilters(query)
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getCharactersWithPagination(): Flow<PagingData<Character>> {
        val pagingSourceFactory = {
            dao.getAllPagedCharacters()
        }

        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = CharactersMediator(
                api = api,
                database = database
            )
        ).flow.map { data ->
            data.map { characterEntity ->
                mapperEntityToDomain.map(characterEntity)
            }
        }
    }


    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            prefetchDistance = 20,
            initialLoadSize = 20,
            maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
            jumpThreshold = Int.MIN_VALUE
        )
    }
}