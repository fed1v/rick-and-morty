package com.example.rickandmorty.data.pagination.locations

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.episodes.EpisodeEntity
import com.example.rickandmorty.data.local.database.episodes.remote_keys.EpisodeRemoteKeys
import com.example.rickandmorty.data.local.database.locations.LocationEntity
import com.example.rickandmorty.data.local.database.locations.remote_keys.LocationRemoteKeys
import com.example.rickandmorty.data.mapper.episode.EpisodeDtoToEpisodeEntityMapper
import com.example.rickandmorty.data.mapper.location.LocationDtoToLocationEntityMapper
import com.example.rickandmorty.data.remote.locations.LocationsApi
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.models.location.LocationFilter

@OptIn(ExperimentalPagingApi::class)
class LocationsFiltersMediator(
    private val api: LocationsApi,
    private val database: RickAndMortyDatabase,
    private val locationFilters: LocationFilter
) : RemoteMediator<Int, LocationEntity>() {

    private val locationsDao = database.locationDao
    private val locationsRemoteKeysDao = database.locationsRemoteKeysDao

    private var startingPage = 1

    private var hiddenLocations = listOf<LocationEntity>()

    override suspend fun initialize(): InitializeAction {
        hiddenLocations = locationsDao.getHiddenLocations()
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult {
        val keyPageData = getKeyPageData(loadType, state)
        val page = when (keyPageData) {
            is MediatorResult.Success -> {
                return keyPageData
            }
            else -> {
                startingPage++
            }
        }

        try {
            val filtersToApply = mapOf(
                "name" to locationFilters.name,
                "type" to locationFilters.type,
                "dimension" to locationFilters.dimension
            ).filter { it.value != null }

            val locationsApiResponse = api.getPagedLocationsByFilters(
                page = page,
                filters = filtersToApply
            )

            val locationsFromApi = locationsApiResponse.results

            val hiddenLocationsIds = hiddenLocations.map { it.id }

            val idsOfHiddenLocationsList = mutableListOf<Int>()
            locationsFromApi.forEach { location ->
                val containsCharacter = hiddenLocationsIds.contains(-location.id)
                if (containsCharacter) idsOfHiddenLocationsList.add(-location.id)
            }

            locationsDao.deleteLocationsByIds(idsOfHiddenLocationsList)
            locationsRemoteKeysDao.deleteKeysByIds(idsOfHiddenLocationsList)

            val isEndOfList = locationsFromApi.isEmpty()
                    || locationsApiResponse.info?.next.isNullOrBlank()
                    || locationsApiResponse.toString().contains("error")

            database.withTransaction {
                val keys = locationsFromApi.map {
                    var prevKey: Int? = (it.id - 1) / 20
                    if (prevKey != null && prevKey <= 0) prevKey = null
                    val nextKey = prevKey?.plus(2) ?: 2

                    LocationRemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                locationsRemoteKeysDao.insertKeys(keys)

                val mapperDtoToEntity = LocationDtoToLocationEntityMapper()
                val locationsEntities = locationsFromApi.map { mapperDtoToEntity.map(it) }

                locationsDao.insertLocations(locationsEntities)
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>,
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                } else {
                    return nextKey
                }
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey != null) {
                    return prevKey
                } else {
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                }
            }
        }
    }

    private suspend fun getFirstRemoteKey(
        state: PagingState<Int, LocationEntity>
    ): LocationRemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { character ->
                locationsRemoteKeysDao.remoteKeysLocationId(character.id)
            }
    }


    private suspend fun getLastRemoteKey(
        state: PagingState<Int, LocationEntity>,
    ): LocationRemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { character ->
                locationsRemoteKeysDao.remoteKeysLocationId(character.id)
            }
    }

    private suspend fun getClosestRemoteKey(
        state: PagingState<Int, LocationEntity>
    ): LocationRemoteKeys? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    locationsRemoteKeysDao.remoteKeysLocationId(id)
                }
            }
    }
}