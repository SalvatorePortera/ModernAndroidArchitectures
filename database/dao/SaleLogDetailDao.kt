package com.nereus.craftbeer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nereus.craftbeer.database.entity.SaleLog
import com.nereus.craftbeer.database.entity.SaleLogDetail

@Dao
interface SaleLogDetailDao {
    /**
     * Insert
     * Do insert in background (suspend)
     * @param saleLogDetail
     */
    @Insert
    suspend fun insert(saleLogDetail: SaleLogDetail)

    /**
     * Insert all
     *
     * @param saleLogDetail
     */
    @Insert
    suspend fun insertAll(saleLogDetail: List<SaleLogDetail>)

    /**
     * Insert without coroutine
     * Do insert in mainthread (just For test)
     * @param saleLogDetail
     */
    @Insert
    fun insertWithoutCoroutine(saleLogDetail: SaleLogDetail)

    /**
     * Update
     * Do update in background (suspend)
     * @param saleLogDetail
     */
    @Update
    suspend fun update(saleLogDetail: SaleLogDetail)
}