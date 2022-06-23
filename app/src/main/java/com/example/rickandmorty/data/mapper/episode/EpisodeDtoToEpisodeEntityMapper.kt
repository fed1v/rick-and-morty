package com.example.rickandmorty.data.mapper.episode

import com.example.rickandmorty.data.local.database.episodes.EpisodeEntity
import com.example.rickandmorty.data.models.episode.EpisodeDto
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class EpisodeDtoToEpisodeEntityMapper : Mapper<EpisodeDto, EpisodeEntity> {

    private val extractor = ExtractIdFromUrlUtil()

    override fun map(data: EpisodeDto): EpisodeEntity = EpisodeEntity(
        id = data.id,
        air_date = data.air_date,
        characters = data.characters.map { extractor.extract(it) ?: -1 },
        episode = data.episode,
        name = data.name
    )
}