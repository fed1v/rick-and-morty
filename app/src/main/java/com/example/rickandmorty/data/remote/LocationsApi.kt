package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.models.location.LocationDto
import com.example.rickandmorty.data.models.location.LocationsApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationsApi {

    @GET("location")
    suspend fun getLocations(): LocationsApiResponse

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto

    @GET("location/{ids}")
    suspend fun getLocationsByIds(@Path("ids") ids: String): List<LocationDto>

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}