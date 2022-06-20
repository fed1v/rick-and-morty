package com.example.rickandmorty.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterData(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val location: LocationData,
    val origin: LocationData
) : Parcelable