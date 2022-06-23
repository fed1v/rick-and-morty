package com.example.rickandmorty.data.local.database.locations

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rickandmorty.data.local.database.converters.IdsConverter

@Entity(tableName = "locations")
@TypeConverters(IdsConverter::class)
data class LocationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val dimension: String,
    val name: String,
    val residents: List<Int>,
    val type: String,
)