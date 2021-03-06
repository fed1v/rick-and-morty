package com.example.rickandmorty.domain.models.character

data class CharacterFilter(
    var name: String? = null,
    var status: String? = null,
    var species: String? = null,
    var type: String? = null,
    var gender: String? = null
)