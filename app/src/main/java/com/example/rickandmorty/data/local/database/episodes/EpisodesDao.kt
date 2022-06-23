package com.example.rickandmorty.data.local.database.episodes

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface EpisodesDao {

    @Query("SELECT * FROM episodes")
    fun getEpisodes(): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id IN (:ids)")
    fun getEpisodesByIds(ids: List<Int>): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id=:id")
    fun getEpisodeById(id: Int): EpisodeEntity

    @Query(
        """
        SELECT * FROM episodes
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:episode IS NULL OR episode LIKE :episode))
        """
    )
    fun getEpisodesByFilters(
        name: String? = null,
        episode: String? = null
    ): List<EpisodeEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: EpisodeEntity)

    @RawQuery
    fun getFilters(query: SupportSQLiteQuery): List<String>
}
