package com.example.rickandmorty.data.local.database.episodes.remote_keys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodesRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<EpisodeRemoteKeys>)

    @Query("SELECT * FROM episode_remote_keys WHERE id = :id")
    suspend fun remoteKeysEpisodesId(id: Int): EpisodeRemoteKeys?

    @Query("SELECT * FROM episode_remote_keys")
    suspend fun getAllKeys(): List<EpisodeRemoteKeys?>

    @Query("SELECT * FROM episode_remote_keys WHERE (id<0)")
    suspend fun getHiddenKeys(): List<EpisodeRemoteKeys>

    @Query("DELETE FROM episode_remote_keys WHERE (id<0)")
    suspend fun clearHiddenKeys()

    @Query("SELECT * FROM episode_remote_keys WHERE (id>:id)")
    suspend fun getKeysFromId(id: Int): List<EpisodeRemoteKeys>

    @Query("DELETE FROM episode_remote_keys WHERE (id>:id)")
    suspend fun deleteKeysFromId(id: Int)

    @Query("DELETE FROM episode_remote_keys WHERE id IN (:ids)")
    suspend fun deleteKeysByIds(ids: List<Int>)
}