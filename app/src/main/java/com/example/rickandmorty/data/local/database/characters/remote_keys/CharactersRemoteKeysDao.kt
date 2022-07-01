package com.example.rickandmorty.data.local.database.characters.remote_keys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharactersRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<CharacterRemoteKeys>)

    @Query("SELECT * FROM character_remote_keys WHERE id = :id")
    suspend fun remoteKeysCharacterId(id: Int): CharacterRemoteKeys?

    @Query("SELECT * FROM character_remote_keys")
    suspend fun getAllKeys(): List<CharacterRemoteKeys?>

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM character_remote_keys WHERE (id>:id)")
    fun getKeysFromId(id: Int): List<CharacterRemoteKeys>

    @Query("DELETE FROM character_remote_keys WHERE (id>:id)")
    suspend fun deleteKeysFromId(id: Int)

    @Query("DELETE FROM character_remote_keys WHERE id IN (:ids)")
    suspend fun deleteKeysByIds(ids: List<Int>)

    @Query("SELECT * FROM character_remote_keys WHERE (id<0)")
    suspend fun getHiddenKeys(): List<CharacterRemoteKeys>

    @Query("DELETE FROM character_remote_keys WHERE (id<0)")
    suspend fun clearHiddenKeys()
}