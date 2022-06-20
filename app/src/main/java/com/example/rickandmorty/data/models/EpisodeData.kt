package com.example.rickandmorty.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EpisodeData(
    val id: Int,
    val name: String,
    val episode: String,
    val airDate: String,
) : Parcelable