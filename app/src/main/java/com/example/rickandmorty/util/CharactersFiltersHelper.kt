package com.example.rickandmorty.util

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CharactersFiltersHelper(
    private val context: Context,
    private val applyCallback: (CharacterFilter) -> Unit
) {

    private var appliedFilter = CharacterFilter()

    private var namesArray = (1..10).map { "name$it" }.toTypedArray()
    private var currentName: String? = null

    private val statusArray = arrayOf(
        "alive",
        "dead",
        "unknown"
    )
    private var currentStatus: String? = null

    private var speciesArray = (1..20).map { "species$it" }.toTypedArray()
    private var currentSpecies: String? = null

    private var typesArray = (1..10).map { "type$it" }.toTypedArray()
    private var currentType: String? = null

    private val gendersArray = arrayOf(
        "female",
        "male",
        "genderless",
        "unknown"
    )
    private var currentGender: String? = null


    fun openFilters() {
        val name = if (appliedFilter.name == null) "Name" else "Name: ${appliedFilter.name}"
        val status =
            if (appliedFilter.status == null) "Status" else "Status: ${appliedFilter.status}"
        val species =
            if (appliedFilter.species == null) "Species" else "Species: ${appliedFilter.species}"
        val type = if (appliedFilter.type == null) "Type" else "Type: ${appliedFilter.type}"
        val gender =
            if (appliedFilter.gender == null) "Gender" else "Gender: ${appliedFilter.gender}"

        val filtersArray = arrayOf(name, status, species, type, gender)

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
                appliedFilter = CharacterFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun openFilter(id: Int) {
        when (id) {
            0 -> openFilterName()
            1 -> openFilterStatus()
            2 -> openFilterSpecies()
            3 -> openFilterType()
            4 -> openFilterGender()
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


    //TODO
    private fun openFilterType() {
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

    //TODO
    private fun openFilterSpecies() {
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

    //TODO
    private fun openFilterName() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Name")
            .setSingleChoiceItems(
                namesArray,
                namesArray.indexOf(appliedFilter.name)
            ) { dialog, which ->
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
            .setNeutralButton("Reset") { dialog, _ ->
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