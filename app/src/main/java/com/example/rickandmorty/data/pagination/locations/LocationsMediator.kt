package com.example.rickandmorty.data.pagination.locations

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.locations.LocationEntity
import com.example.rickandmorty.data.local.database.locations.remote_keys.LocationRemoteKeys
import com.example.rickandmorty.data.mapper.location.LocationDtoToLocationEntityMapper
import com.example.rickandmorty.data.remote.locations.LocationsApi

@ExperimentalPagingApi
class LocationsMediator(
    private val api: LocationsApi,
    private val database: RickAndMortyDatabase
) : RemoteMediator<Int, LocationEntity>() {

    private val locationsDao = database.locationDao
    private val locationsRemoteKeysDao = database.locationsRemoteKeysDao

    private var startPaginationFromIndex: Int = -1

    private var locationsToRemember = mutableListOf<LocationEntity>()
    private var keysToRemember = mutableListOf<LocationRemoteKeys>()

    private var hiddenLocations = mutableListOf<LocationEntity>()
    private var hiddenKeys = mutableListOf<LocationRemoteKeys>()

    override suspend fun initialize(): InitializeAction {
        var rememberFromId = -1

        val keys = locationsRemoteKeysDao.getAllKeys()

        for (i in keys.indices) {
            if (i + 1 <= keys.size - 1) {
                if ((keys[i]?.id ?: -1) > 0
                    && (keys[i + 1]?.id ?: -1) > 0
                    && keys[i]?.id != keys[i + 1]?.id?.minus(1)
                ) {
                    rememberFromId = keys[i]?.id ?: -1
                    startPaginationFromIndex = keys[i]?.id ?: -1
                    break
                }
            }
        }

        if (keys.isNotEmpty() && rememberFromId == -1) {
            if ((keys[keys.lastIndex]?.id?.rem(20) ?: -1) != 0) {
                rememberFromId = keys[keys.lastIndex]?.id ?: 0
                rememberFromId /= 20
                rememberFromId *= 20
            }
        }

        database.withTransaction {
            val hidLocations = locationsDao.getHiddenLocations()
            hiddenLocations.addAll(hidLocations)

            val hidKeys = locationsRemoteKeysDao.getHiddenKeys()
            hiddenKeys.addAll(hidKeys)
        }


        if (rememberFromId != -1) {
            rememberFromId /= 20
            rememberFromId *= 20

            database.withTransaction {
                val locationsToDelete = locationsDao.getLocationsFromId(rememberFromId)
                locationsToRemember.addAll(locationsToDelete)

                val keysToDelete = locationsRemoteKeysDao.getKeysFromId(rememberFromId)
                keysToRemember.addAll(keysToDelete)

                val locationsToHide = locationsToDelete.map { it.copy(id = -it.id) }
                locationsDao.insertLocations(locationsToHide)

                val keysToHide = keysToDelete.map { it.copy(id = -it.id) }
                locationsRemoteKeysDao.insertKeys(keysToHide)

                locationsDao.deleteLocationsFromId(rememberFromId)
                locationsRemoteKeysDao.deleteKeysFromId(rememberFromId)
            }
        }


        return super.initialize()
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult {
        val pageKeyData = getKeyPageData(loadType, state)
        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val locationsApiResponse = api.getPagedLocations(page = page)
            val locationsFromApi = locationsApiResponse.results

            val isEndOfList = locationsFromApi.isEmpty()
                    || locationsApiResponse.info?.next.isNullOrBlank()
                    || locationsApiResponse.toString().contains("error")


            database.withTransaction {
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = locationsFromApi.map {
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

                if (isEndOfList) {
                    locationsRemoteKeysDao.insertKeys(hiddenKeys)
                    locationsDao.insertLocations(hiddenLocations)

                    locationsDao.clearHiddenLocations()
                    locationsRemoteKeysDao.clearHiddenKeys()
                }
            }

            return MediatorResult.Success(endOfPaginationReached = isEndOfList)

        } catch (e: Exception) {
            database.withTransaction {
                locationsDao.insertLocations(locationsToRemember)
                locationsRemoteKeysDao.insertKeys(keysToRemember)

                locationsDao.insertLocations(hiddenLocations.map { it.copy(id = -it.id) })
                locationsRemoteKeysDao.insertKeys(hiddenKeys.map { it.copy(id = -it.id) })

                locationsDao.clearHiddenLocations()
                locationsRemoteKeysDao.clearHiddenKeys()
            }

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
                return nextKey ?: MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                return prevKey ?: MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
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
            ?.let { episode ->
                locationsRemoteKeysDao.remoteKeysLocationId(episode.id)
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