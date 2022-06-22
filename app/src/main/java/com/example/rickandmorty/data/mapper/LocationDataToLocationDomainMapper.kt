package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.models.location.LocationDto
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.util.ExtractIdFromUrlUtil
import com.example.rickandmorty.util.mapper.Mapper

class LocationDataToLocationDomainMapper : Mapper<LocationDto, Location> {

    override fun map(data: LocationDto): Location = Location(
        id = data.id,
        name = data.name,
        type = data.type,
        dimension = data.dimension,
        residents = data.residents.map { ExtractIdFromUrlUtil().extract(it) }
    )
}