package com.example.rickandmorty.domain.models.location

data class LocationFilter(
    var name: String? = null,
    var type: String? = null,
    var dimension: String? = null
)