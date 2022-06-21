package com.example.rickandmorty.data.models.character

data class CharacterDto(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: LocationData,
    val name: String,
    val origin: LocationData,
    val species: String,
    val status: String,
    val type: String,
    val url: String
) {

    data class LocationData(
        val name: String,
        val url: String
    )
}