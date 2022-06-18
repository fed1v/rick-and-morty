package com.example.rickandmorty.util

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LocationsFiltersHelper(
    private val context: Context,
    private val applyCallback: (LocationFilter) -> Unit
) {

    private var appliedFilter = LocationFilter()

    private var namesArray = (1..10).map { "name$it" }.toTypedArray()
    private var currentName: String? = null

    private var typesArray = (1..10).map { "type$it" }.toTypedArray()
    private var currentType: String? = null

    private var dimensionsArray = (1..10).map { "dimension$it" }.toTypedArray()
    private var currentDimension: String? = null


    fun openFilters() {
        val name = if (appliedFilter.name == null) "Name" else "Name: ${appliedFilter.name}"
        val type = if (appliedFilter.type == null) "Type" else "Type: ${appliedFilter.type}"
        val dimension =
            if (appliedFilter.dimension == null) "Dimension" else "Dimension: ${appliedFilter.dimension}"

        val filtersArray = arrayOf(name, type, dimension)

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
                appliedFilter = LocationFilter()
                dialog.dismiss()
            }
            .show()
    }

    private fun openFilter(id: Int) {
        when (id) {
            0 -> openFilterName()
            1 -> openFilterType()
            2 -> openFilterDimension()
        }
    }

    private fun openFilterDimension() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Dimension")
            .setSingleChoiceItems(
                dimensionsArray,
                dimensionsArray.indexOf(appliedFilter.dimension)
            ) { dialog, which ->
                currentDimension = dimensionsArray[which]
            }
            .setPositiveButton("Ok") { dialog, _ ->
                appliedFilter.dimension = currentDimension
                currentDimension = null
                returnToPreviousDialog(dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                currentDimension = null
                returnToPreviousDialog(dialog)
            }
            .setNeutralButton("Reset") { dialog, _ ->
                appliedFilter.dimension = null
                returnToPreviousDialog(dialog)
            }
            .show()
    }


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