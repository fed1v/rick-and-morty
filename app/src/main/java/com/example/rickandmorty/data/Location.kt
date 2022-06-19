package com.example.rickandmorty.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
) : Parcelable