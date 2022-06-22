package com.example.rickandmorty.presentation.mapper

import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.util.mapper.Mapper

class EpisodeDomainToEpisodePresentationModelMapper : Mapper<Episode, EpisodePresentation> {

    override fun map(data: Episode): EpisodePresentation = EpisodePresentation(
        id = data.id,
        name = data.name,
        episode = data.episode,
        airDate = data.air_date,
        characters = data.characters
    )
}