package com.example.rickandmorty.data.local.providers

import com.example.rickandmorty.presentation.models.LocationPresentation

class LocationsProvider {
    companion object {
        val locationsList = generateList()

        private fun generateList(): List<LocationPresentation> {
            val resultList = mutableListOf<LocationPresentation>()
            (1..100).forEach {
                resultList.add(
                    LocationPresentation(
                        id = it,
                        name = "name$it",
                        type = "type$it",
                        dimension = "dimension$it",
                        residents = listOf()
                    )
                )
            }
            return resultList
        }
    }
}