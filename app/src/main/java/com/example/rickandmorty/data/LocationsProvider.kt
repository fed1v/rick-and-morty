package com.example.rickandmorty.data

class LocationsProvider {
    companion object {
        val locationsList = generateList()

        private fun generateList(): List<Location> {
            val resultList = mutableListOf<Location>()
            (1..100).forEach {
                resultList.add(
                    Location(
                        id = it,
                        name = "name$it",
                        type = "type$it",
                        dimension = "dimension$it"
                    )
                )
            }
            return resultList
        }
    }
}