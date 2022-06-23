package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.local.database.episodes.EpisodeEntity
import com.example.rickandmorty.domain.models.episode.Episode
import com.example.rickandmorty.util.mapper.Mapper

class EpisodeEntityToEpisodeDomainMapper : Mapper<EpisodeEntity, Episode> {

    override fun map(data: EpisodeEntity): Episode = Episode(
        id = data.id,
        name = data.name,
        air_date = data.air_date,
        episode = data.episode,
        characters = data.characters
    )
}