package com.example.rickandmorty.presentation.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LocationPresentation(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
) : Parcelable