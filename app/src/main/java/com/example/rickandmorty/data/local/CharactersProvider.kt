package com.example.rickandmorty.data.local

import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.LocationPresentation

class CharactersProvider {
    companion object {
        val charactersList = generateList()

        private fun generateList(): List<CharacterPresentation> {
            val resultList = mutableListOf<CharacterPresentation>()
            (1..100).forEach {
                resultList.add(
                    CharacterPresentation(
                        id = it,
                        name = "name$it",
                        status = "status$it",
                        species = "species$it",
                        gender = "gender$it",
                        location = LocationPresentation(it, "locationName$it", "locationType$it", "locationDimension$it"),
                        origin = LocationPresentation(it, "originName$it", "originType$it", "originDimension$it"),
                        episodes = (1..10).toList()
                    )
                )
            }
            return resultList
        }
    }
}