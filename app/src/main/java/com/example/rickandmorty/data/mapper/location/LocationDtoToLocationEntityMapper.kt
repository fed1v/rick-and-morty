package com.example.rickandmorty.data.mapper.location

import com.example.rickandmorty.data.local.database.locations.LocationEntity
import com.example.rickandmorty.data.models.location.LocationDto
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class LocationDtoToLocationEntityMapper : Mapper<LocationDto, LocationEntity> {

    private val extractor = ExtractIdFromUrlUtil()

    override fun map(data: LocationDto): LocationEntity = LocationEntity(
        id = data.id,
        dimension = data.dimension,
        name = data.name,
        residents = data.residents.map { extractor.extract(it) ?: -1 },
        type = data.type
    )
}