package com.example.rickandmorty.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode(
    val id: Int,
    val name: String,
    val episode: String,
    val airDate: String,
) : Parcelable