package com.example.rickandmorty.data.local.database.episodes

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface EpisodesDao {

    @Query("SELECT * FROM episodes")
    fun getEpisodes(): List<EpisodeEntity>

    @Query("""
        SELECT * FROM episodes 
        WHERE id IN (:ids)
        OR -id IN (:ids)
    """)
    fun getEpisodesByIds(ids: List<Int>): List<EpisodeEntity>

    @Query("""
            SELECT * FROM episodes 
            WHERE id=:id 
            OR -id=:id
    """)
    fun getEpisodeById(id: Int): EpisodeEntity

    @Query("""
        SELECT * FROM episodes
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:episode IS NULL OR episode LIKE :episode))
    """)
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

    @Query("SELECT * FROM episodes WHERE (id<0)")
    suspend fun getHiddenEpisodes(): List<EpisodeEntity>

    @Query("DELETE FROM episodes WHERE (id<0)")
    suspend fun clearHiddenEpisodes()

    @Query("SELECT * FROM episodes WHERE (id>:id)")
    suspend fun getEpisodesFromId(id: Int): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE (id>:id)")
    fun getPagedEpisodesFromId(id: Int): PagingSource<Int, EpisodeEntity>

    @Query("""
        SELECT * FROM episodes
        WHERE ((:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:episode IS NULL OR episode LIKE :episode))
    """)
    fun getPagedEpisodesByFilters(
        name: String? = null,
        episode: String? = null
    ): PagingSource<Int, EpisodeEntity>

    @Query("DELETE FROM episodes WHERE (id>:id)")
    suspend fun deleteEpisodesFromId(id: Int)

    @Query("DELETE FROM episodes WHERE id IN (:ids)")
    suspend fun deleteEpisodesByIds(ids: List<Int>)
}
