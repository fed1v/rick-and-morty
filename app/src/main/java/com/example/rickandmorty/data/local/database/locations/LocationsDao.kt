package com.example.rickandmorty.data.local.database.locations

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    fun getLocationsByIds(ids: List<Int>): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id=:id")
    fun getLocationById(id: Int): LocationEntity?

    @Query(
        """
        SELECT * FROM locations
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND(:type IS NULL OR type LIKE :type)
        AND(:dimension IS NULL OR dimension LIKE :dimension))
        """
    )
    fun getLocationsByFilters(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ): List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity)

    @RawQuery
    fun getFilters(query: SupportSQLiteQuery): List<String>
}