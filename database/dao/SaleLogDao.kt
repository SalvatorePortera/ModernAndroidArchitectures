package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.SaleLog

@Dao
interface SaleLogDao {
    /**
     * Insert
     * Do insert in background (suspend)
     * @param saleLog
     * @return
     */
    @Insert
    suspend fun insert(saleLog: SaleLog): Long

    /**
     * Insert without coroutine
     * Do insert in mainthread (just For test)
     * @param saleLog
     */
    @Insert
    fun insertWithoutCoroutine(saleLog: SaleLog)

    /**
     * Update
     * Do update in background (suspend)
     * @param saleLog
     */
    @Update
    suspend fun update(saleLog: SaleLog)

    /**
     * Delete
     *
     * @param saleLog
     */
    @Delete
    suspend fun delete(saleLog: SaleLog)

    /**
     * Get
     *
     * @param key
     * @return
     */
    @Query("SELECT * from sale_logs WHERE sale_log_id = :key")
    fun get(key: Long): SaleLog

    /**
     * Get all
     *
     * @return
     */
    @Query("SELECT * from sale_logs")
    suspend fun getAll(): List<SaleLog>
}