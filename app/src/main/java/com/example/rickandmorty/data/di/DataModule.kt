package com.example.rickandmorty.data.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.api.characters.CharactersApi
import com.example.rickandmorty.data.remote.api.episodes.EpisodesApi
import com.example.rickandmorty.data.remote.api.locations.LocationsApi
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.data.repository.LocationsRepositoryImpl
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.repository.LocationsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class DataModule(val context: Context) {

    @Provides
    @Singleton
    fun provideCharactersApi(): CharactersApi {
        return Retrofit.Builder()
            .baseUrl(CharactersApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CharactersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEpisodesApi(): EpisodesApi {
        return Retrofit.Builder()
            .baseUrl(CharactersApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EpisodesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationsApi(): LocationsApi {
        return Retrofit.Builder()
            .baseUrl(CharactersApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRickAndMortyDatabase(): RickAndMortyDatabase {
        return RickAndMortyDatabase.getInstance(context)
    }

    @Provides
    fun provideCharactersRepository(
        api: CharactersApi,
        database: RickAndMortyDatabase
    ): CharactersRepository {
        return CharactersRepositoryImpl(
            api = api,
            database = database
        )
    }

    @Provides
    fun provideEpisodesRepository(
        api: EpisodesApi,
        database: RickAndMortyDatabase
    ): EpisodesRepository {
        return EpisodesRepositoryImpl(
            api = api,
            database = database
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    fun provideLocationRepository(
        api: LocationsApi,
        database: RickAndMortyDatabase
    ): LocationsRepository {
        return LocationsRepositoryImpl(
            api = api,
            database = database
        )
    }
}