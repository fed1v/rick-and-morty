package com.example.rickandmorty.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EpisodePresentation (
    val id: Int,
    val name: String,
    val episode: String,
    val airDate: String,
) : Parcelable