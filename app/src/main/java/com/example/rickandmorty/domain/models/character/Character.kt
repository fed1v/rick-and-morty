package com.example.rickandmorty.domain.models.character

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: CharacterLocation,
    val location: CharacterLocation,
    val episodes: List<Int?>,
    val image: String
) {

    data class CharacterLocation(
        val id: Int,
        val name: String
    )
}