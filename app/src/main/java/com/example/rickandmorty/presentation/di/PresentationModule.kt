package com.example.rickandmorty.presentation.di

import com.example.rickandmorty.domain.usecases.characters.*
import com.example.rickandmorty.domain.usecases.episodes.*
import com.example.rickandmorty.domain.usecases.locations.*
import com.example.rickandmorty.presentation.ui.characters.list.CharactersViewModelFactory
import com.example.rickandmorty.presentation.ui.episodes.list.EpisodesViewModelFactory
import com.example.rickandmorty.presentation.ui.locations.list.LocationsViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {

    @Provides
    fun provideCharactersViewModelFactory(
        getCharactersUseCase: GetCharactersUseCase,
        getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase,
        getCharactersFiltersUseCase: GetCharactersFiltersUseCase,
        getCharactersWithPaginationUseCase: GetCharactersWithPaginationUseCase,
        getCharactersByFiltersWithPaginationUseCase: GetCharactersByFiltersWithPaginationUseCase
    ): CharactersViewModelFactory {
        return CharactersViewModelFactory(
            getCharactersUseCase = getCharactersUseCase,
            getCharactersByFiltersUseCase = getCharactersByFiltersUseCase,
            getCharactersFiltersUseCase = getCharactersFiltersUseCase,
            getCharactersWithPaginationUseCase = getCharactersWithPaginationUseCase,
            getCharactersByFiltersWithPaginationUseCase = getCharactersByFiltersWithPaginationUseCase
        )
    }

    @Provides
    fun provideEpisodesViewModelFactory(
        getEpisodesUseCase: GetEpisodesUseCase,
        getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase,
        getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase,
        getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase,
        getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase
    ): EpisodesViewModelFactory {
        return EpisodesViewModelFactory(
            getEpisodesUseCase = getEpisodesUseCase,
            getEpisodesByFiltersUseCase = getEpisodesByFiltersUseCase,
            getEpisodesFiltersUseCase = getEpisodesFiltersUseCase,
            getEpisodesWithPaginationUseCase = getEpisodesWithPaginationUseCase,
            getEpisodesByFiltersWithPaginationUseCase = getEpisodesByFiltersWithPaginationUseCase
        )
    }

    @Provides
    fun provideLocationsViewModelFactory(
        getLocationsUseCase: GetLocationsUseCase,
        getLocationsByFiltersUseCase: GetLocationsByFiltersUseCase,
        getLocationsFiltersUseCase: GetLocationsFiltersUseCase,
        getLocationsWithPaginationUseCase: GetLocationsWithPaginationUseCase,
        getLocationsByFiltersWithPaginationUseCase: GetLocationsByFiltersWithPaginationUseCase
    ): LocationsViewModelFactory {
        return LocationsViewModelFactory(
            getLocationsUseCase = getLocationsUseCase,
            getLocationsByFiltersUseCase = getLocationsByFiltersUseCase,
            getLocationsFiltersUseCase = getLocationsFiltersUseCase,
            getLocationsWithPaginationUseCase = getLocationsWithPaginationUseCase,
            getLocationsByFiltersWithPaginationUseCase = getLocationsByFiltersWithPaginationUseCase
        )
    }


}