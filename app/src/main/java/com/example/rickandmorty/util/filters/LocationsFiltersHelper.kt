package com.example.rickandmorty.util.filters

import android.content.Context
import android.content.DialogInterface
import com.example.rickandmorty.R
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LocationsFiltersHelper(
    private val context: Context,
    private val applyCallback: (LocationFilter) -> Unit,
    private val resetCallback: () -> Unit
) {

    private var appliedFilter = LocationFilter()

    var typesArray = arrayOf(
        "unknown",
        "Planet",
        "Cluster",
        "Space station",
        "TV",
        "Resort",
        "Fantasy town"
    )
    private var currentType: String? = null


    var dimensionsArray = arrayOf(
        "unknown",
        "Dimension C-137",
        "Replacement Dimension",
        "Dimension 5-126"
    )
    private var currentDimension: String? = null


    fun openFilters() {
        val type = if (appliedFilter.type == null) "Type" else "Type: ${appliedFilter.type}"
        val dimension =
            if (appliedFilter.dimension == null) "Dimension" else "Dimension: ${appliedFilter.dimension}"

        val filtersArray = arrayOf(/*name,*/ type, dimension)

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
            0 -> openFilterType()
            1 -> openFilterDimension()
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


    private fun returnToPreviousDialog(dialog: DialogInterface) {
        openFilters()
        dialog.dismiss()
    }

    private fun applyFilters() {
        applyCallback(appliedFilter)
    }

    private fun resetFilters() {
        appliedFilter = LocationFilter()
        resetCallback()
    }
}