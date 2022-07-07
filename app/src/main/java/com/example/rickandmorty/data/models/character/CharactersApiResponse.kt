package com.example.rickandmorty.data.models.character

import com.example.rickandmorty.data.models.Info

data class CharactersApiResponse(
    val info: Info?,
    val results: List<CharacterDto>
)