package com.example.rickandmorty.data.local.database.episodes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodesDao {

    @Query("SELECT * FROM episodes")
    fun getEpisodes(): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id IN (:ids)")
    fun getEpisodesByIds(ids: List<Int>): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id=:id")
    fun getEpisodeById(id: Int): EpisodeEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: EpisodeEntity)
}
