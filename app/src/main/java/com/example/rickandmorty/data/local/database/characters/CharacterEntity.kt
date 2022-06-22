package com.example.rickandmorty.data.local.database.characters

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rickandmorty.data.local.database.converters.EpisodeIdsConverter

@Entity(tableName = "characters")
@TypeConverters(EpisodeIdsConverter::class)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,

    @Embedded(prefix = "origin")
    val origin: CharacterEntityLocation,

    @Embedded(prefix = "location")
    val location: CharacterEntityLocation,

    val episodes: List<Int>,

    val image: String
) {

    data class CharacterEntityLocation(
        val id: Int,
        val name: String
    )
}
