package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDataToCharacterDomainMapper : Mapper<CharacterDto, Character> {

    private val extractor = ExtractIdFromUrlUtil()

    override fun map(data: CharacterDto): Character = Character(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        origin = Character.CharacterLocation(
            id = extractor.extract(data.origin.url) ?: -1,
            name = data.origin.name
        ),
        location = Character.CharacterLocation(
            id = extractor.extract(data.location.url) ?: -1,
            name = data.location.name,
        ),
        episodes = data.episode.map { extractor.extract(it) },
        image = data.image
    )
}