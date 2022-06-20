package com.example.rickandmorty.domain.models.character

data class CharacterFilter(
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String
)