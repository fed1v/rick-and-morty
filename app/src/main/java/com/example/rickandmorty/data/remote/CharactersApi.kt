package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.data.models.character.CharactersApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CharactersApi {

    @GET("character")
    suspend fun getCharacters(): CharactersApiResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    @GET("character/{ids}")
    suspend fun getCharactersByIds(@Path("ids") ids: String): List<CharacterDto>

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}