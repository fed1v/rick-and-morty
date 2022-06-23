package com.example.rickandmorty.data.local.database.locations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations")
    fun getLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    fun getLocationsByIds(ids: List<Int>): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id=:id")
    fun getLocationById(id: Int): LocationEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity)
}