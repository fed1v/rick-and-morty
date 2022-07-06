package com.example.rickandmorty.data.remote.api.episodes

import com.example.rickandmorty.data.models.episode.EpisodeDto
import com.example.rickandmorty.data.models.episode.EpisodesApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface EpisodesApi {

    @GET("episode")
    suspend fun getEpisodes(): EpisodesApiResponse

    @GET("episode/{id}")
    suspend fun getEpisodeById(@Path("id") id: Int): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(@Path("ids") ids: String): List<EpisodeDto>

    @GET("episode")
    suspend fun getEpisodesByFilters(@QueryMap filters: Map<String, String?>): EpisodesApiResponse

    @GET("episode")
    suspend fun getPagedEpisodes(@Query("page") page: Int): EpisodesApiResponse

    @GET("episode")
    suspend fun getPagedEpisodesByFilters(
        @Query("page") page: Int,
        @QueryMap filters: Map<String, String?>
    ): EpisodesApiResponse

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}