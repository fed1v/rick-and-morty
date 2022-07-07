package com.example.rickandmorty.presentation.mapper

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDomainToCharacterPresentationMapper : Mapper<Character, CharacterPresentation> {

    override fun map(data: Character): CharacterPresentation = CharacterPresentation(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        gender = data.gender,
        type = data.type,
        location = LocationPresentation(
            id = data.location.id,
            name = data.location.name,
            type = "unknown",
            dimension = "unknown",
            residents = listOf()
        ),
        origin = LocationPresentation(
            id = data.origin.id,
            name = data.origin.name,
            type = "unknown",
            dimension = "unknown",
            residents = listOf()
        ),
        episodes = data.episodes,
        image = data.image
    )
}