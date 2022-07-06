package com.example.rickandmorty.presentation.di

import com.example.rickandmorty.domain.usecases.characters.*
import com.example.rickandmorty.domain.usecases.episodes.*
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsByFiltersWithPaginationUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsFiltersUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationsWithPaginationUseCase
import com.example.rickandmorty.presentation.ui.characters.details.viewmodel.CharacterDetailsViewModelFactory
import com.example.rickandmorty.presentation.ui.characters.list.viewmodel.CharactersViewModelFactory
import com.example.rickandmorty.presentation.ui.episodes.details.viewmodel.EpisodeDetailsViewModelFactory
import com.example.rickandmorty.presentation.ui.episodes.list.viewmodel.EpisodesViewModelFactory
import com.example.rickandmorty.presentation.ui.locations.details.viewmodel.LocationDetailsViewModelFactory
import com.example.rickandmorty.presentation.ui.locations.list.viewmodel.LocationsViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {

    // Characters

    @Provides
    fun provideCharactersViewModelFactory(
        getCharactersFiltersUseCase: GetCharactersFiltersUseCase,
        getCharactersWithPaginationUseCase: GetCharactersWithPaginationUseCase,
        getCharactersByFiltersWithPaginationUseCase: GetCharactersByFiltersWithPaginationUseCase
    ): CharactersViewModelFactory {
        return CharactersViewModelFactory(
            getCharactersFiltersUseCase = getCharactersFiltersUseCase,
            getCharactersWithPaginationUseCase = getCharactersWithPaginationUseCase,
            getCharactersByFiltersWithPaginationUseCase = getCharactersByFiltersWithPaginationUseCase
        )
    }

    @Provides
    fun provideCharacterDetailsViewModelFactory(
        getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase,
        getCharacterByIdUseCase: GetCharacterByIdUseCase,
        getLocationByIdUseCase: GetLocationByIdUseCase
    ): CharacterDetailsViewModelFactory {
        return CharacterDetailsViewModelFactory(
            getEpisodesByIdsUseCase = getEpisodesByIdsUseCase,
            getCharacterByIdUseCase = getCharacterByIdUseCase,
            getLocationByIdUseCase = getLocationByIdUseCase
        )
    }

    // Episodes

    @Provides
    fun provideEpisodesViewModelFactory(
        getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase,
        getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase,
        getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase
    ): EpisodesViewModelFactory {
        return EpisodesViewModelFactory(
            getEpisodesFiltersUseCase = getEpisodesFiltersUseCase,
            getEpisodesWithPaginationUseCase = getEpisodesWithPaginationUseCase,
            getEpisodesByFiltersWithPaginationUseCase = getEpisodesByFiltersWithPaginationUseCase
        )
    }

    @Provides
    fun provideEpisodeDetailsViewModelFactory(
        getEpisodeByIdUseCase: GetEpisodeByIdUseCase,
        getCharactersByIdsUseCase: GetCharactersByIdsUseCase
    ): EpisodeDetailsViewModelFactory {
        return EpisodeDetailsViewModelFactory(
            getEpisodeByIdUseCase = getEpisodeByIdUseCase,
            getCharactersByIdsUseCase = getCharactersByIdsUseCase
        )
    }

    // Locations

    @Provides
    fun provideLocationsViewModelFactory(
        getLocationsFiltersUseCase: GetLocationsFiltersUseCase,
        getLocationsWithPaginationUseCase: GetLocationsWithPaginationUseCase,
        getLocationsByFiltersWithPaginationUseCase: GetLocationsByFiltersWithPaginationUseCase
    ): LocationsViewModelFactory {
        return LocationsViewModelFactory(
            getLocationsFiltersUseCase = getLocationsFiltersUseCase,
            getLocationsWithPaginationUseCase = getLocationsWithPaginationUseCase,
            getLocationsByFiltersWithPaginationUseCase = getLocationsByFiltersWithPaginationUseCase
        )
    }

    @Provides
    fun provideLocationDetailsViewModelFactory(
        getLocationByIdUseCase: GetLocationByIdUseCase,
        getCharactersByIdsUseCase: GetCharactersByIdsUseCase
    ): LocationDetailsViewModelFactory {
        return LocationDetailsViewModelFactory(
            getLocationByIdUseCase = getLocationByIdUseCase,
            getCharactersByIdsUseCase = getCharactersByIdsUseCase
        )
    }
}