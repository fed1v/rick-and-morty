package com.example.rickandmorty.data.pagination

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)