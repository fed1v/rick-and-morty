package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDtoToCharacterEntityMapper : Mapper<CharacterDto, CharacterEntity> {

    override fun map(data: CharacterDto): CharacterEntity = CharacterEntity(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        originString = data.origin.name,
        locationString = data.location.name,
        episodesIds = data.episode.joinToString(","),
        image = data.image
    )
}