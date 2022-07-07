package com.example.rickandmorty.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPresentation(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<Int?>
) : Parcelable