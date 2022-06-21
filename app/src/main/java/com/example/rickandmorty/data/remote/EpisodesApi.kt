package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.models.episode.EpisodeDto
import com.example.rickandmorty.data.models.episode.EpisodesApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodesApi {

    @GET("episode")
    suspend fun getEpisodes(): EpisodesApiResponse

    @GET("episode/{id}")
    suspend fun getEpisodeById(@Path("id") id: Int): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(@Path("ids") ids: String): List<EpisodeDto>

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}