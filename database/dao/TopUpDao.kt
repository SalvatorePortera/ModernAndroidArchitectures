package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.TopUp

@Dao
interface TopUpDao {
    // Do insert in background (suspend)
    @Insert
    suspend fun insert(topUp: TopUp): Long

    // Do update in background (suspend)
    @Update
    suspend fun update(topUp: TopUp)

    @Delete
    suspend fun delete(topUp: TopUp)

    @Query("SELECT * from top_up WHERE top_up_id = :key")
    fun get(key: Long): TopUp

    @Query("SELECT * from top_up")
    suspend fun getAll(): List<TopUp>
}