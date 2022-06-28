package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.ErrorLog
import com.nereus.craftbeer.database.entity.MessageLog

/**
 * Error log dao
 *
 * コンストラクタ  ErrorLogDao
 */
@Dao
interface ErrorLogDao {
    // Do insert in background (suspend)
    @Insert
    suspend fun insert(errorLog: ErrorLog): Long

    // Do update in background (suspend)
    @Update
    suspend fun update(errorLog: ErrorLog)

    /**
     * Delete
     *
     * @param errorLog
     */
    @Delete
    suspend fun delete(errorLog: ErrorLog)

    /**
     * Get
     *
     * @param key
     * @return
     */
    @Query("SELECT * from error_logs WHERE error_log_id = :key")
    fun get(key: Long): ErrorLog

    /**
     * Get all
     *
     * @return
     */
    @Query("SELECT * from error_logs")
    suspend fun getAll(): List<ErrorLog>
}