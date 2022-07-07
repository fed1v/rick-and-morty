package com.example.rickandmorty.util.filters

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CharactersFiltersHelper(
    private val context: Context,
    private val applyCallback: (CharacterFilter) -> Unit,
    private val resetCallback: () -> Unit
) {

    private var appliedFilter = CharacterFilter()

    private val statusArray = arrayOf(
        "Alive",
        "Dead",
        "Unknown"
    )
    private var currentStatus: String? = null


    var speciesArray = arrayOf(
        "Human",
        "Humanoid",
        "Alien",
        "Poopybutthole",
        "Cronenberg"
    )
    private var currentSpecies: String? = null


    var typesArray = arrayOf(
        "",
        "Genetic experiment"
    )
    private var currentType: String? = null

    private val gendersArray = arrayOf(
        "Female",
        "Male",
        "Genderless",
        "Unknown"
    )
    private var currentGender: String? = null


    fun openFilters() {
        val status =
            if (appliedFilter.status == null) "Status" else "Status: ${appliedFilter.status}"
        val species =
            if (appliedFilter.species == null) "Species" else "Species: ${appliedFilter.species}"
        val type = if (appliedFilter.type == null) "Type" else "Type: ${appliedFilter.type}"
        val gender =
            if (appliedFilter.gender == null) "Gender" else "Gender: ${appliedFilter.gender}"

        val filtersArray = arrayOf(/*name,*/ status, species, type, gender)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.filter)
            .setItems(filtersArray) { dialog, which ->
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
            0 -> openFilterStatus()
            1 -> openFilterSpecies()
            2 -> openFilterType()
            3 -> openFilterGender()
        }
    }

    private fun openFilterGender() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Gender")
            .setSingleChoiceItems(
                gendersArray,
                gendersArray.indexOf(appliedFilter.gender)
            ) { dialog, which ->
                currentGender = gendersArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.gender = currentGender
                currentGender = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentGender = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.gender = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }


    private fun openFilterType() {
        typesArray.sortBy { it }
        MaterialAlertDialogBuilder(context)
            .setTitle("Type")
            .setSingleChoiceItems(
                typesArray,
                typesArray.indexOf(appliedFilter.type)
            ) { dialog, which ->
                currentType = typesArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.type = currentType
                currentType = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentType = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.type = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }

    private fun openFilterSpecies() {
        speciesArray.sortBy { it }
        MaterialAlertDialogBuilder(context)
            .setTitle("Species")
            .setSingleChoiceItems(
                speciesArray,
                speciesArray.indexOf(appliedFilter.species)
            ) { dialog, which ->
                currentSpecies = speciesArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.species = currentSpecies
                currentSpecies = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentSpecies = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.species = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }

    private fun openFilterStatus() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Status")
            .setSingleChoiceItems(
                statusArray,
                statusArray.indexOf(appliedFilter.status)
            ) { dialog, which ->
                currentStatus = statusArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.status = currentStatus
                currentStatus = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentStatus = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.status = null
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
        appliedFilter = CharacterFilter()
        resetCallback()
    }
}