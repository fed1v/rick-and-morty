package com.example.rickandmorty.util.mapper

interface Mapper<T, R> {

    fun map(data: T): R
}