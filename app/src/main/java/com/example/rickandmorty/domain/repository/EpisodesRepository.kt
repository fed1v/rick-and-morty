package com.example.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {

    suspend fun getEpisodeById(id: Int): Episode

    suspend fun getEpisodesByIds(ids: String): List<Episode>

    suspend fun getFilters(filterName: String): List<String>

    suspend fun getEpisodesWithPagination(): Flow<PagingData<Episode>>

    suspend fun getEpisodesByFiltersWithPagination(filters: EpisodeFilter): Flow<PagingData<Episode>>
}