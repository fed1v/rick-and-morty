package com.example.rickandmorty.data.remote.characters

import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.data.models.character.CharactersApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CharactersApi {

    @GET("character")
    suspend fun getCharacters(): CharactersApiResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    @GET("character/{ids}")
    suspend fun getCharactersByIds(@Path("ids") ids: String): List<CharacterDto>

    @GET("character")
    suspend fun getCharactersByFilters(@QueryMap filters: Map<String, String?>): CharactersApiResponse


    @GET("character")
    suspend fun getPagedCharacters(@Query("page") page: Int): CharactersApiResponse

    @GET("character")
    suspend fun getPagedCharactersByFilters(
        @Query("page") page: Int,
        @QueryMap filters: Map<String, String?>
    ): CharactersApiResponse

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}