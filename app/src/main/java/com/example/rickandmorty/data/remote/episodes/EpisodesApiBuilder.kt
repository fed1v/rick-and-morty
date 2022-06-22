package com.example.rickandmorty.data.remote.episodes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object EpisodesApiBuilder {

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(EpisodesApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = getRetrofit().create(EpisodesApi::class.java)
}