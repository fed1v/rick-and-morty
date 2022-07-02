package com.example.rickandmorty.di

import androidx.paging.ExperimentalPagingApi
import com.example.rickandmorty.data.di.DataModule
import com.example.rickandmorty.domain.di.DomainModule
import com.example.rickandmorty.presentation.di.PresentationModule
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.characters.list.CharactersListFragment
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.episodes.list.EpisodesListFragment
import com.example.rickandmorty.presentation.ui.locations.details.LocationDetailsFragment
import com.example.rickandmorty.presentation.ui.locations.list.LocationsListFragment
import dagger.Component
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
        PresentationModule::class
    ]
)
interface AppComponent {

    fun inject(charactersListFragment: CharactersListFragment)
    fun inject(episodesListFragment: EpisodesListFragment)
    fun inject(locationsListFragment: LocationsListFragment)

    fun inject(characterDetailsFragment: CharacterDetailsFragment)
    fun inject(episodeDetailsFragment: EpisodeDetailsFragment)
    fun inject(locationDetailsFragment: LocationDetailsFragment)

}