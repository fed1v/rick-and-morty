package com.example.rickandmorty.data.local.database.characters

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharactersDao {

    @Query("SELECT * FROM characters")
    fun getCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters where id IN (:ids)")
    fun getCharactersByIds(ids: List<Int>): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id=:id")
    fun getCharacterById(id: Int): CharacterEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: CharacterEntity)
}