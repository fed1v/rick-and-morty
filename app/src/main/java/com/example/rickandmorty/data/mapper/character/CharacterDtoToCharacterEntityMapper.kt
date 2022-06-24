package com.example.rickandmorty.data.mapper.character

import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDtoToCharacterEntityMapper : Mapper<CharacterDto, CharacterEntity> {

    private val extractor = ExtractIdFromUrlUtil()

    override fun map(data: CharacterDto): CharacterEntity = CharacterEntity(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        origin = CharacterEntity.CharacterEntityLocation(
            id = extractor.extract(data.origin.url) ?: -1,
            name = data.origin.name
        ),
        location = CharacterEntity.CharacterEntityLocation(
            id = extractor.extract(data.location.url) ?: -1,
            name = data.location.name
        ),
        episodes = data.episode.map { extractor.extract(it) ?: -1 },
        image = data.image
    )
}