package com.example.rickandmorty.data.local.database.locations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int
)