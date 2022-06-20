package com.example.rickandmorty.presentation.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CharacterPresentation(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val location: LocationPresentation,
    val origin: LocationPresentation
) : Parcelable