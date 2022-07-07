package com.example.rickandmorty.presentation.mapper

import com.example.rickandmorty.domain.models.location.Location
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.mapper.Mapper

class LocationDomainToLocationPresentationMapper : Mapper<Location, LocationPresentation> {

    override fun map(data: Location): LocationPresentation = LocationPresentation(
        id = data.id,
        name = data.name,
        type = data.type,
        dimension = data.dimension,
        residents = data.residents
    )
}