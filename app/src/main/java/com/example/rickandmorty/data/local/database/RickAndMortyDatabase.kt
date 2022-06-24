package com.example.rickandmorty.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.local.database.episodes.EpisodeEntity
import com.example.rickandmorty.data.local.database.locations.LocationsDao
import com.example.rickandmorty.data.local.database.locations.LocationEntity

@Database(
    version = 1, entities = [
        CharacterEntity::class,
        EpisodeEntity::class,
        LocationEntity::class
    ]
)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract val charactersDao: CharactersDao
    abstract val episodesDao: EpisodesDao
    abstract val locationDao: LocationsDao

    companion object {
        private var INSTANCE: RickAndMortyDatabase? = null
        private const val DB_NAME = "rick_and_morty_db"

        fun getInstance(context: Context): RickAndMortyDatabase {
            if (INSTANCE == null) {
                synchronized(RickAndMortyDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        RickAndMortyDatabase::class.java,
                        DB_NAME
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }

}