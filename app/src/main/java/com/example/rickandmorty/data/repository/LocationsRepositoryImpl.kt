package com.example.rickandmorty.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.local.database.locations.LocationsDao
import com.example.rickandmorty.data.mapper.location.LocationDtoToLocationEntityMapper
import com.example.rickandmorty.data.mapper.location.LocationEntityToLocationDomainMapper
import com.example.rickandmorty.data.remote.locations.LocationsApi
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository

class LocationsRepositoryImpl(
    private val api: LocationsApi,
    private val dao: LocationsDao
) : LocationsRepository {

    private val mapperDtoToEntity = LocationDtoToLocationEntityMapper()
    private val mapperEntityToDomain = LocationEntityToLocationDomainMapper()

    override suspend fun getLocations(): List<Location> {
        try {
            val locationsFromApi = api.getLocations().results
            val locationsEntities = locationsFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertLocations(locationsEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val locationsFromDB = dao.getLocations()

        return locationsFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getLocationById(id: Int): Location {
        try {
            val locationFromApi = api.getLocationById(id)
            val locationEntity = mapperDtoToEntity.map(locationFromApi)
            dao.insertLocation(locationEntity)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val locationFromDB = dao.getLocationById(id)
        if (locationFromDB == null) return Location(-1, "?", "?", "?", emptyList())

        return mapperEntityToDomain.map(locationFromDB)
    }

    override suspend fun getLocationsByIds(ids: String): List<Location> {
        try {
            val locationsFromApi = api.getLocationsByIds(ids)
            val locationsEntities = locationsFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertLocations(locationsEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val idsList = IdsConverter().fromStringIds(ids)
        val locationsFromDB = dao.getLocationsByIds(idsList)

        return locationsFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getLocationsByFilters(filters: LocationFilter): List<Location> {
        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "type" to filters.type,
            "dimension" to filters.dimension
        ).filter { it.value != null }

        try {
            val locationsFromApi = api.getLocationsByFilters(filtersToApply)
            val locationsEntities = locationsFromApi.results.map { mapperDtoToEntity.map(it) }
            dao.insertLocations(locationsEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val locationsFromDB = dao.getLocationsByFilters(
            name = filtersToApply["name"],
            type = filtersToApply["type"],
            dimension = filtersToApply["dimension"]
        )

        return locationsFromDB.map { mapperEntityToDomain.map(it) }
    }

    override suspend fun getFilters(filterName: String): List<String> {
        val query = SimpleSQLiteQuery("SELECT DISTINCT $filterName FROM locations")
        return dao.getFilters(query)
    }
}