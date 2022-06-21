package com.example.rickandmorty.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LocationsApiBuilder {

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(EpisodesApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = getRetrofit().create(LocationsApi::class.java)
}