package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.TopUp

@Dao
interface TopUpDao {
    /**
     * Insert
     * Do insert in background (suspend)
     * @param topUp
     * @return
     */
    @Insert
    suspend fun insert(topUp: TopUp): Long

    /**
     * Update
     * Do update in background (suspend)
     * @param topUp
     */
    @Update
    suspend fun update(topUp: TopUp)

    /**
     * Delete
     *
     * @param topUp
     */
    @Delete
    suspend fun delete(topUp: TopUp)

    /**
     * Get
     *
     * @param key
     * @return
     */
    @Query("SELECT * from top_up WHERE top_up_id = :key")
    fun get(key: Long): TopUp

    /**
     * Get all
     *
     * @return
     */
    @Query("SELECT * from top_up")
    suspend fun getAll(): List<TopUp>
}