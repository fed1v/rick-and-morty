package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.models.episode.EpisodeDto
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.util.mapper.Mapper

class EpisodeDataToEpisodeModelMapper: Mapper<EpisodeDto, Episode> {

    override fun map(data: EpisodeDto): Episode = Episode(
        id = data.id,
        name = data.name,
        air_date = data.air_date,
        episode = data.episode,
        created = data.created
    )
}