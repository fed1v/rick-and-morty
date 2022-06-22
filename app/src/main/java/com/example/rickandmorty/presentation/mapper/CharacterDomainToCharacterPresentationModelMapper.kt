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
        location = LocationPresentation(
            id = data.location.id,
            name = data.location.name,
            type = data.location.type,
            dimension = data.location.dimension,
            residents = data.location.residents
        ), // TODO
        origin = LocationPresentation(
            id = data.origin.id,
            name = data.origin.name,
            type = data.origin.type,
            dimension = data.origin.dimension,
            residents = data.origin.residents
        ),
        episodes = data.episodes,
        image = data.image
    )
}