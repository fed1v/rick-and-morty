package com.example.rickandmorty.data.local.database.episodes.remote_keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_remote_keys")
data class EpisodeRemoteKeys(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)