package com.nereus.craftbeer.database.dao

import androidx.room.*
import com.nereus.craftbeer.database.entity.SaleLog

@Dao
interface SaleLogDao {
    // Do insert in background (suspend)
    @Insert
    suspend fun insert(saleLog: SaleLog): Long

    // Do insert in mainthread (just For test)
    @Insert
    fun insertWithoutCoroutine(saleLog: SaleLog)

    // Do update in background (suspend)
    @Update
    suspend fun update(saleLog: SaleLog)

    @Delete
    suspend fun delete(saleLog: SaleLog)

    @Query("SELECT * from sale_logs WHERE sale_log_id = :key")
    fun get(key: Long): SaleLog

    @Query("SELECT * from sale_logs")
    suspend fun getAll(): List<SaleLog>
}