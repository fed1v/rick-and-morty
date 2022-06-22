package com.example.rickandmorty.data.local.database.characters

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    //  val origin: Location,
    val originString: String,
    //  val location: Location,
    val locationString: String,
    //  val episodes: List<Int?>,
    val episodesIds: String,

    val image: String
)