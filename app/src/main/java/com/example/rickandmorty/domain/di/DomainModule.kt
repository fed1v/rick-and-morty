package com.example.rickandmorty.domain.di

import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.repository.LocationsRepository
import com.example.rickandmorty.domain.usecases.characters.*
import com.example.rickandmorty.domain.usecases.episodes.*
import com.example.rickandmorty.domain.usecases.locations.*
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    // Characters UseCases

    @Provides
    fun provideGetCharacterByIdUseCase(
        repository: CharactersRepository
    ): GetCharacterByIdUseCase {
        return GetCharacterByIdUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersByFiltersUseCase(
        repository: CharactersRepository
    ): GetCharactersByFiltersUseCase {
        return GetCharactersByFiltersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersByFiltersWithPaginationUseCase(
        repository: CharactersRepository
    ): GetCharactersByFiltersWithPaginationUseCase {
        return GetCharactersByFiltersWithPaginationUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersByIdsUseCase(
        repository: CharactersRepository
    ): GetCharactersByIdsUseCase {
        return GetCharactersByIdsUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersFiltersUseCase(
        repository: CharactersRepository
    ): GetCharactersFiltersUseCase {
        return GetCharactersFiltersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersUseCase(
        repository: CharactersRepository
    ): GetCharactersUseCase {
        return GetCharactersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetCharactersWithPaginationUseCase(
        repository: CharactersRepository
    ): GetCharactersWithPaginationUseCase {
        return GetCharactersWithPaginationUseCase(
            repository = repository
        )
    }

    // Episodes UseCases

    @Provides
    fun provideGetEpisodeByIdUseCase(
        repository: EpisodesRepository
    ): GetEpisodeByIdUseCase {
        return GetEpisodeByIdUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesByFiltersUseCase(
        repository: EpisodesRepository
    ): GetEpisodesByFiltersUseCase {
        return GetEpisodesByFiltersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesByFiltersWithPaginationUseCase(
        repository: EpisodesRepository
    ): GetEpisodesByFiltersWithPaginationUseCase {
        return GetEpisodesByFiltersWithPaginationUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesByIdsUseCase(
        repository: EpisodesRepository
    ): GetEpisodesByIdsUseCase {
        return GetEpisodesByIdsUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesFiltersUseCase(
        repository: EpisodesRepository
    ): GetEpisodesFiltersUseCase {
        return GetEpisodesFiltersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesUseCase(
        repository: EpisodesRepository
    ): GetEpisodesUseCase {
        return GetEpisodesUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetEpisodesWithPaginationUseCase(
        repository: EpisodesRepository
    ): GetEpisodesWithPaginationUseCase {
        return GetEpisodesWithPaginationUseCase(
            repository = repository
        )
    }

    // Locations UseCases

    @Provides
    fun provideGetLocationByIdUseCase(
        repository: LocationsRepository
    ): GetLocationByIdUseCase {
        return GetLocationByIdUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLocationsByFiltersUseCase(
        repository: LocationsRepository
    ): GetLocationsByFiltersUseCase {
        return GetLocationsByFiltersUseCase(
            repository = repository
        )
    }


    @Provides
    fun provideGetLocationsByFiltersWithPaginationUseCase(
        repository: LocationsRepository
    ): GetLocationsByFiltersWithPaginationUseCase {
        return GetLocationsByFiltersWithPaginationUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLocationsByIdsUseCase(
        repository: LocationsRepository
    ): GetLocationsByIdsUseCase {
        return GetLocationsByIdsUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLocationsFiltersUseCase(
        repository: LocationsRepository
    ): GetLocationsFiltersUseCase {
        return GetLocationsFiltersUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLocationsUseCase(
        repository: LocationsRepository
    ): GetLocationsUseCase {
        return GetLocationsUseCase(
            repository = repository
        )
    }

    @Provides
    fun provideGetLocationsWithPaginationUseCase(
        repository: LocationsRepository
    ): GetLocationsWithPaginationUseCase {
        return GetLocationsWithPaginationUseCase(
            repository = repository
        )
    }
}