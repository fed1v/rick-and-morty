package com.example.rickandmorty.data.remote.locations

import com.example.rickandmorty.data.models.location.LocationDto
import com.example.rickandmorty.data.models.location.LocationsApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface LocationsApi {

    @GET("location")
    suspend fun getLocations(): LocationsApiResponse

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto

    @GET("location/{ids}")
    suspend fun getLocationsByIds(@Path("ids") ids: String): List<LocationDto>

    @GET("location")
    suspend fun getLocationsByFilters(@QueryMap filters: Map<String, String?>): LocationsApiResponse

    @GET("location")
    suspend fun getPagedLocations(@Query("page") page: Int): LocationsApiResponse

    @GET("location")
    suspend fun getPagedLocationsByFilters(
        @Query("page") page: Int,
        @QueryMap filters: Map<String, String?>
    ): LocationsApiResponse

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}