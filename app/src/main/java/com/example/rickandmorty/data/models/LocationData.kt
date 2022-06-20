package com.example.rickandmorty.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
) : Parcelable