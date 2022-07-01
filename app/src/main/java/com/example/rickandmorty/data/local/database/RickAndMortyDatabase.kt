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
import com.example.rickandmorty.data.local.database.characters.remote_keys.CharactersRemoteKeysDao
import com.example.rickandmorty.data.local.database.characters.remote_keys.CharacterRemoteKeys
import com.example.rickandmorty.data.local.database.episodes.remote_keys.EpisodeRemoteKeys
import com.example.rickandmorty.data.local.database.episodes.remote_keys.EpisodesRemoteKeysDao
import com.example.rickandmorty.data.local.database.locations.remote_keys.LocationRemoteKeys
import com.example.rickandmorty.data.local.database.locations.remote_keys.LocationsRemoteKeysDao

@Database(
    version = 1,
    entities = [
        CharacterEntity::class,
        EpisodeEntity::class,
        LocationEntity::class,
        CharacterRemoteKeys::class,
        EpisodeRemoteKeys::class,
        LocationRemoteKeys::class
    ]
)
abstract class RickAndMortyDatabase : RoomDatabase() {

    abstract val charactersDao: CharactersDao
    abstract val episodesDao: EpisodesDao
    abstract val locationDao: LocationsDao

    abstract val charactersRemoteKeysDao: CharactersRemoteKeysDao
    abstract val episodesRemoteKeysDao: EpisodesRemoteKeysDao
    abstract val locationsRemoteKeysDao: LocationsRemoteKeysDao

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