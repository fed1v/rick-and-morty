package com.example.rickandmorty.data.local

import com.example.rickandmorty.presentation.models.EpisodePresentation

class EpisodesProvider {
    companion object {
        val episodesList = generateList()

        private fun generateList(): List<EpisodePresentation> {
            val resultList = mutableListOf<EpisodePresentation>()
            (1..100).forEach {
                resultList.add(
                    EpisodePresentation(
                        id = it,
                        name = "name$it",
                        episode = "episode$it",
                        airDate = "airDate$it",
                        listOf()
                    )
                )
            }
            return resultList
        }
    }
}