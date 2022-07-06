package com.example.rickandmorty.data.repository

import androidx.paging.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.converters.IdsConverter
import com.example.rickandmorty.data.local.database.locations.remote_keys.LocationRemoteKeys
import com.example.rickandmorty.data.mapper.location.LocationDtoToLocationEntityMapper
import com.example.rickandmorty.data.mapper.location.LocationEntityToLocationDomainMapper
import com.example.rickandmorty.data.pagination.locations.LocationsFiltersMediator
import com.example.rickandmorty.data.pagination.locations.LocationsMediator
import com.example.rickandmorty.data.remote.api.locations.LocationsApi
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.domain.repository.LocationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ExperimentalPagingApi
class LocationsRepositoryImpl(
    private val api: LocationsApi,
    private val database: RickAndMortyDatabase
) : LocationsRepository {

    private val dao = database.locationDao
    private val locationsRemoteKeysDao = database.locationsRemoteKeysDao

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

            var prevKey: Int? = (locationFromApi.id - 1) / 20
            if (prevKey != null && prevKey <= 0) prevKey = null
            val nextKey = (prevKey?.plus(2)) ?: 2

            val key = LocationRemoteKeys(
                id = locationFromApi.id,
                prevKey = prevKey,
                nextKey = nextKey
            )

            locationsRemoteKeysDao.insertKeys(listOf(key))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val locationFromDB =
            dao.getLocationById(id) ?: return Location(-1, "?", "?", "?", emptyList())

        return mapperEntityToDomain.map(locationFromDB)
    }

    override suspend fun getLocationsByIds(ids: String): List<Location> {
        try {
            val locationsFromApi = api.getLocationsByIds(ids)
            val locationsEntities = locationsFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertLocations(locationsEntities)

            val keys = locationsFromApi.map {
                var prevKey: Int? = (it.id - 1) / 20
                if (prevKey != null && prevKey <= 0) prevKey = null
                val nextKey = (prevKey?.plus(2)) ?: 2

                LocationRemoteKeys(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            locationsRemoteKeysDao.insertKeys(keys)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val idsList = IdsConverter().fromStringIds(ids)
        val locationsFromDB = dao.getLocationsByIds(idsList)

        return locationsFromDB.map { mapperEntityToDomain.map(it) }
            .distinctBy { it.id }
            .sortedBy { it.id }
    }

    override suspend fun getLocationsByFilters(filters: LocationFilter): List<Location> {
        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "type" to filters.type,
            "dimension" to filters.dimension
        ).filter { it.value != null }

        try {
            val locationsFromApi = api.getLocationsByFilters(filtersToApply).results
            val locationsEntities = locationsFromApi.map { mapperDtoToEntity.map(it) }
            dao.insertLocations(locationsEntities)

            val keys = locationsFromApi.map {
                var prevKey: Int? = (it.id - 1) / 20
                if (prevKey != null && prevKey <= 0) prevKey = null
                val nextKey = (prevKey?.plus(2)) ?: 2

                LocationRemoteKeys(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            locationsRemoteKeysDao.insertKeys(keys)
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


    override suspend fun getLocationsWithPagination(): Flow<PagingData<Location>> {
        val pagingSourceFactory = {
            dao.getPagedLocationsFromId(0)
        }

        return Pager(
            pagingSourceFactory = pagingSourceFactory,
            config = getDefaultPageConfig(),
            remoteMediator = LocationsMediator(
                api = api,
                database = database
            )
        ).flow.map { data ->
            data.map { locationEntity ->
                mapperEntityToDomain.map(locationEntity)
            }
        }
    }

    override suspend fun getLocationsByFiltersWithPagination(filters: LocationFilter): Flow<PagingData<Location>> {

        val filtersToApply = mapOf<String, String?>(
            "name" to filters.name,
            "type" to filters.type,
            "dimension" to filters.dimension
        ).filter { it.value != null }

        val pagingSourceFactory = {
            dao.getPagedCharactersByFilters(
                name = filtersToApply["name"],
                type = filtersToApply["type"],
                dimension = filtersToApply["dimension"]
            )
        }

        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = LocationsFiltersMediator(
                api = api,
                database = database,
                locationFilters = filters
            )
        ).flow.map { data ->
            data.map { locationEntity ->
                mapperEntityToDomain.map(locationEntity)
            }
        }
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            prefetchDistance = 10,
            initialLoadSize = 20,
            maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
            jumpThreshold = Int.MIN_VALUE
        )
    }
}