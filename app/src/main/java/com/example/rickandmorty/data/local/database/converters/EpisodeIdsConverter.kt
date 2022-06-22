package com.example.rickandmorty.data.local.database.converters

import androidx.room.TypeConverter

class EpisodeIdsConverter {

    @TypeConverter
    fun toStringIds(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromStringIds(ids: String): List<Int> {
        return ids.split(",").map { it.toInt() }
    }
}