package com.example.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.character.CharacterFilter
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {

    suspend fun getCharacterById(id: Int): Character

    suspend fun getCharactersByIds(ids: String): List<Character>

    suspend fun getFilters(filterName: String): List<String>

    suspend fun getCharactersWithPagination(): Flow<PagingData<Character>>

    suspend fun getCharactersByFiltersWithPagination(filters: CharacterFilter): Flow<PagingData<Character>>
}