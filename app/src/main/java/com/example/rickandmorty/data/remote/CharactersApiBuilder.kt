package com.example.rickandmorty.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CharactersApiBuilder {

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(CharactersApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = getRetrofit().create(CharactersApi::class.java)
}