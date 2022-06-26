package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.ErrorLog
import com.nereus.craftbeer.database.entity.MessageLog

@Dao
interface ErrorLogDao {
    // Do insert in background (suspend)
    @Insert
    suspend fun insert(errorLog: ErrorLog): Long

    // Do update in background (suspend)
    @Update
    suspend fun update(errorLog: ErrorLog)

    @Delete
    suspend fun delete(errorLog: ErrorLog)

    @Query("SELECT * from error_logs WHERE error_log_id = :key")
    fun get(key: Long): ErrorLog

    @Query("SELECT * from error_logs")
    suspend fun getAll(): List<ErrorLog>
}