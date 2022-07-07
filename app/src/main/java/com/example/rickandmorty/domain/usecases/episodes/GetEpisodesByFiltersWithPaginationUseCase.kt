package com.example.rickandmorty.domain.usecases.episodes

import androidx.paging.PagingData
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository
import kotlinx.coroutines.flow.Flow

class GetEpisodesByFiltersWithPaginationUseCase(
    private val repository: EpisodesRepository
) {

    suspend fun execute(filters: EpisodeFilter): Flow<PagingData<Episode>> {
        return repository.getEpisodesByFiltersWithPagination(filters)
    }
}