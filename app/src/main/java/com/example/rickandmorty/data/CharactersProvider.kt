package com.example.rickandmorty.data

class CharactersProvider {
    companion object {
        val charactersList = generateList()

        private fun generateList(): List<Character> {
            val resultList = mutableListOf<Character>()
            (1..100).forEach {
                resultList.add(
                    Character(
                        id = it,
                        name = "name$it",
                        status = "status$it",
                        species = "species$it",
                        gender = "gender$it",
                        location = Location(it, "locationName$it", "locationType$it", "locationDimension$it"),
                        origin = Location(it, "originName$it", "originType$it", "originDimension$it"),
                    )
                )
            }
            return resultList
        }
    }
}