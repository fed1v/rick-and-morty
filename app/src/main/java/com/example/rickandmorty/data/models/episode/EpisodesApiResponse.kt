package com.example.rickandmorty.data.models.episode

import com.example.rickandmorty.data.models.Info

data class EpisodesApiResponse(
    val info: Info?,
    val results: List<EpisodeDto>
)