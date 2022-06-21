package com.example.rickandmorty.presentation.mapper

import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDomainToCharacterPresentationModelMapper : Mapper<Character, CharacterPresentation> {

    override fun map(data: Character): CharacterPresentation = CharacterPresentation(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        gender = data.gender,
        location = LocationPresentation(-1, data.location, "unknown", "unknown"), // TODO
        origin = LocationPresentation(-1, data.origin, "unknown", "unknown"),
        episodes = data.episodes,
        image = data.image
    )
}