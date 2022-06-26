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
    // Do insert in background (suspend)
    @Insert
    suspend fun insert(saleLogDetail: SaleLogDetail)

    @Insert
    suspend fun insertAll(saleLogDetail: List<SaleLogDetail>)

    // Do insert in mainthread (just For test)
    @Insert
    fun insertWithoutCoroutine(saleLogDetail: SaleLogDetail)

    // Do update in background (suspend)
    @Update
    suspend fun update(saleLogDetail: SaleLogDetail)
}