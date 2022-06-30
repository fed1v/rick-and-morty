package com.example.rickandmorty.data.local.database.locations.remote_keys

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LocationsRemoteKeysDao {

    @Query("SELECT * FROM location_remote_keys WHERE id = :id")
    suspend fun remoteKeysLocationId(id: Int): LocationRemoteKeys?

}