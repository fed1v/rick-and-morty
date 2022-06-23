package com.example.rickandmorty.data.mapper.character

import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.util.mapper.Mapper

class CharacterEntityToCharacterDomainMapper : Mapper<CharacterEntity, Character> {

    override fun map(data: CharacterEntity): Character = Character(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        origin = Character.CharacterLocation(
            id = data.origin.id,
            name = data.origin.name
        ),
        location = Character.CharacterLocation(
            id = data.location.id,
            name = data.location.name
        ),
        episodes = data.episodes,
        image = data.image
    )
}