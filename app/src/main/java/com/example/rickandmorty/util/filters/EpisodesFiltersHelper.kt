package com.example.rickandmorty.util.filters

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EpisodesFiltersHelper (
    private val context: Context,
    private val applyCallback: (EpisodeFilter) -> Unit
) {

    private var appliedFilter = EpisodeFilter()

    private var namesArray = (1..10).map { "name$it" }.toTypedArray()
    private var currentName: String? = null

    private var episodesArray = (1..10).map { "episode$it" }.toTypedArray()
    private var currentEpisode: String? = null


    fun openFilters() {
        val name = if (appliedFilter.name == null) "Name" else "Name: ${appliedFilter.name}"
        val episode = if (appliedFilter.episode == null) "Episode" else "Episode: ${appliedFilter.episode}"

        val filtersArray = arrayOf(name, episode)

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
            .setNeutralButton("Reset"){ dialog, _ ->
                appliedFilter = EpisodeFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun openFilter(id: Int) {
        when (id) {
            0 -> openFilterName()
            1 -> openFilterEpisode()
        }
    }


    private fun openFilterEpisode() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Type")
            .setSingleChoiceItems(episodesArray, episodesArray.indexOf(appliedFilter.episode)){ dialog, which ->
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
            .setNeutralButton("Reset"){ dialog, _ ->
                appliedFilter.episode = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }


    private fun openFilterName() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Name")
            .setSingleChoiceItems(namesArray, namesArray.indexOf(appliedFilter.name)){ dialog, which ->
                currentName = namesArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.name = currentName
                currentName = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentName = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset"){ dialog, _ ->
                appliedFilter.name = null
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

}