package com.example.rickandmorty.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterPresentation(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val type: String,
    val location: LocationPresentation,
    val origin: LocationPresentation,
    val episodes: List<Int?>,
    val image: String
) : Parcelable