package com.example.rickandmorty.data.models.location

import com.example.rickandmorty.data.models.Info

data class LocationsApiResponse(
    val info: Info?,
    val results: List<LocationDto>
)