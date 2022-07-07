package com.example.rickandmorty.data.local.database.locations

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    fun getLocationsByIds(ids: List<Int>): List<LocationEntity>

    @Query("""
        SELECT * FROM locations 
        WHERE id=:id
        OR -id=:id
        """)
    fun getLocationById(id: Int): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity)

    @RawQuery
    fun getFilters(query: SupportSQLiteQuery): List<String>

    @Query("SELECT * FROM locations WHERE (id<0)")
    suspend fun getHiddenLocations(): List<LocationEntity>

    @Query("DELETE FROM locations WHERE (id<0)")
    fun clearHiddenLocations()

    @Query("SELECT * FROM locations WHERE (id>:id)")
    fun getLocationsFromId(id: Int): List<LocationEntity>

    @Query("DELETE FROM locations WHERE (id>:id)")
    fun deleteLocationsFromId(id: Int)

    @Query("DELETE FROM locations WHERE id IN (:ids)")
    suspend fun deleteLocationsByIds(ids: List<Int>)

    @Query("SELECT * FROM locations WHERE (id>:id)")
    fun getPagedLocationsFromId(id: Int): PagingSource<Int, LocationEntity>

    @Query("""
        SELECT * FROM locations
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND(:type IS NULL OR type LIKE :type)
        AND(:dimension IS NULL OR dimension LIKE :dimension))
    """)
    fun getPagedLocationsByFilters(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ): PagingSource<Int, LocationEntity>
}