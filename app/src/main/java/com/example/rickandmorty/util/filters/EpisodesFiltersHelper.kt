package com.example.rickandmorty.util.filters

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EpisodesFiltersHelper(
    private val context: Context,
    private val applyCallback: (EpisodeFilter) -> Unit,
    private val resetCallback: () -> Unit
) {

    private var appliedFilter = EpisodeFilter()

    // TODO
    private var episodesArray = arrayOf(
        "S01E03",
        "S01",
        "S02",
        "E04",
        "S03E03"
    )
    private var currentEpisode: String? = null


    fun openFilters() {
        val episode =
            if (appliedFilter.episode == null) "Episode" else "Episode: ${appliedFilter.episode}"

        val filtersArray = arrayOf(/*name,*/ episode)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.filter)
            .setItems(filtersArray) { dialog, which ->
                println(which)
                dialog.dismiss()
                openFilter(which)
            }
            .setPositiveButton("Apply") { dialog, _ ->
                applyFilters()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Reset") { dialog, _ ->
                resetFilters()
                dialog.dismiss()
            }
            .show()
    }

    private fun openFilter(id: Int) {
        when (id) {
            0 -> openFilterEpisode()
        }
    }


    private fun openFilterEpisode() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Type")
            .setSingleChoiceItems(
                episodesArray,
                episodesArray.indexOf(appliedFilter.episode)
            ) { dialog, which ->
                currentEpisode = episodesArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.episode = currentEpisode
                currentEpisode = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentEpisode = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.episode = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }

    private fun returnToPreviousDialog(dialog: DialogInterface) {
        openFilters()
        dialog.dismiss()
    }

    private fun applyFilters() {
        applyCallback(appliedFilter)
    }

    private fun resetFilters() {
        appliedFilter = EpisodeFilter()
        resetCallback()
    }

}