package com.example.rickandmorty.data

class EpisodesProvider {
    companion object {
        val episodesList = generateList()

        private fun generateList(): List<Episode> {
            val resultList = mutableListOf<Episode>()
            (1..100).forEach {
                resultList.add(
                    Episode(
                        id = it,
                        name = "name$it",
                        episode = "episode$it",
                        airDate = "airDate$it",
                    )
                )
            }
            return resultList
        }
    }
}