package com.example.rickandmorty.data.local.database.episodes

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rickandmorty.data.local.database.converters.IdsConverter


@Entity(tableName = "episodes")
@TypeConverters(IdsConverter::class)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val air_date: String,
    val characters: List<Int>,
    val episode: String,
    val name: String,
)