package com.example.rickandmorty.data.local.database.converters

import androidx.room.TypeConverter

class IdsConverter {

    @TypeConverter
    fun toStringIds(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromStringIds(ids: String): List<Int> {
        if (ids.isBlank()) return emptyList()
        return ids.split(",").map { it.toInt() }
    }
}