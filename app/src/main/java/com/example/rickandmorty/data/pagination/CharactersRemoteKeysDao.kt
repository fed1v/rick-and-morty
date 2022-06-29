package com.example.rickandmorty.data.pagination

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharactersRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE id = :id")
    suspend fun remoteKeysCharacterId(id: Int): RemoteKeys?

    @Query("SELECT * FROM remotekeys")
    suspend fun getAllKeys(): List<RemoteKeys?>

    @Query("DELETE FROM remotekeys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM remotekeys WHERE (id>:id)")
    fun getKeysFromIndex(id: Int): List<RemoteKeys>

    @Query("DELETE FROM remotekeys WHERE (id>:id)")
    suspend fun deleteKeysFromId(id: Int)

    @Query("DELETE FROM remotekeys WHERE id IN (:ids)")
    suspend fun deleteKeysByIds(ids: List<Int>)

    @Query("SELECT * FROM remotekeys WHERE (id<0)")
    suspend fun getHiddenKeys(): List<RemoteKeys>

    @Query("DELETE FROM remotekeys WHERE (id<0)")
    suspend fun clearHiddenKeys()
}