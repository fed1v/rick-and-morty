package com.example.rickandmorty.data.local.database.episodes

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int
)