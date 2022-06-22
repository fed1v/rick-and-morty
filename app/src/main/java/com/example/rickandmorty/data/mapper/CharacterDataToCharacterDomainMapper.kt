package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDataToCharacterDomainMapper : Mapper<CharacterDto, Character> {

    override fun map(data: CharacterDto): Character = Character(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        origin = Location(
            id = ExtractIdFromUrlUtil().extract(data.origin.url) ?: -1,
            name = data.origin.name,
            type = "unknown",
            dimension = "unknown",
            residents = listOf()
        ),
        location = Location(
            id = ExtractIdFromUrlUtil().extract(data.location.url) ?: -1,
            name = data.location.name,
            type = "unknown",
            dimension = "unknown",
            residents = listOf(),
        ),
        episodes = data.episode.map { ExtractIdFromUrlUtil().extract(it) },
        image = data.image
    )
}