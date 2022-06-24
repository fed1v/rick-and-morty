package com.example.rickandmorty.data.mapper.location

import com.example.rickandmorty.data.local.database.locations.LocationEntity
import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.util.mapper.Mapper

class LocationEntityToLocationDomainMapper : Mapper<LocationEntity, Location> {

    override fun map(data: LocationEntity): Location = Location(
        id = data.id,
        name = data.name,
        type = data.type,
        dimension = data.dimension,
        residents = data.residents
    )
}