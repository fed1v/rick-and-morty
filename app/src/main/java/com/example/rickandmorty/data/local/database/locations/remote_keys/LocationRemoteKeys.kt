package com.example.rickandmorty.data.local.database.locations.remote_keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_remote_keys")
data class LocationRemoteKeys(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)