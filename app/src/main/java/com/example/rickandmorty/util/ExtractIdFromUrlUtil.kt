package com.example.rickandmorty.util

class ExtractIdFromUrlUtil {

    fun extract(url: String): Int? {
        return url.filter { it.isDigit() }.toIntOrNull()
    }
}