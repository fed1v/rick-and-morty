package com.example.rickandmorty.data.local.database.characters

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface CharactersDao {

    @Query("SELECT * FROM characters")
    fun getCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters where id IN (:ids)")
    fun getCharactersByIds(ids: List<Int>): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id=:id")
    fun getCharacterById(id: Int): CharacterEntity

    @Query(
        """
        SELECT * FROM characters
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:status IS NULL OR status LIKE :status)
        AND (:species IS NULL OR species LIKE :species)
        AND (:type IS NULL OR type LIKE :type)
        AND (:gender IS NULL OR gender LIKE :gender))
    """
    )
    fun getCharactersByFilters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null
    ): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: CharacterEntity)

    @RawQuery
    fun getFilters(query: SupportSQLiteQuery): List<String>


    @Query("SELECT * FROM characters")
    fun getAllPagedCharacters(): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters")
    fun clearCharacters()
}