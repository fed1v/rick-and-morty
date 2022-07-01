package com.example.rickandmorty.data.local.database.locations.remote_keys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationsRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<LocationRemoteKeys>)

    @Query("SELECT * FROM location_remote_keys WHERE id = :id")
    suspend fun remoteKeysLocationId(id: Int): LocationRemoteKeys?

    @Query("SELECT * FROM location_remote_keys")
    suspend fun getAllKeys(): List<LocationRemoteKeys?>

    @Query("SELECT * FROM location_remote_keys WHERE (id<0)")
    suspend fun getHiddenKeys(): List<LocationRemoteKeys>

    @Query("DELETE FROM location_remote_keys WHERE (id<0)")
    suspend fun clearHiddenKeys()

    @Query("SELECT * FROM location_remote_keys WHERE (id>:id)")
    suspend fun getKeysFromId(id: Int): List<LocationRemoteKeys>

    @Query("DELETE FROM location_remote_keys WHERE (id>:id)")
    suspend fun deleteKeysFromId(id: Int)

    @Query("DELETE FROM location_remote_keys WHERE id IN (:ids)")
    suspend fun deleteKeysByIds(ids: List<Int>)
}