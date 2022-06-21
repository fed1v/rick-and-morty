package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.util.mapper.Mapper

class CharacterDataToCharacterDomainModelMapper : Mapper<CharacterDto, Character> {

    private fun extractEpisodeIdFromUrl(url: String): Int? {
        return url.filter { it.isDigit() }.toIntOrNull()
    }

    override fun map(data: CharacterDto): Character = Character(
        id = data.id,
        name = data.name,
        status = data.status,
        species = data.species,
        type = data.type,
        gender = data.gender,
        origin = data.origin.name,
        location = data.location.name,
        episodes = data.episode.map { extractEpisodeIdFromUrl(it) },
        image = data.image
    )
}