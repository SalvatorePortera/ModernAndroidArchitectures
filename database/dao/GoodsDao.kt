package com.nereus.craftbeer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nereus.craftbeer.database.entity.Goods

@Dao
interface GoodsDao {
    // Do insert in background (suspend)
    @Insert
    suspend fun insertMultiple(goodsList: List<Goods>)

    // Do insert in mainthread (just For test)
    @Insert
    fun insertWithoutCoroutine(goods: Goods)

    // Do update in background (suspend)
    @Update
    suspend fun update(goods: Goods)

    // Do update in background (suspend)
    @Query("DELETE FROM goods")
    suspend fun deleteAll()

    @Query("SELECT * from goods WHERE jan_code = :janCode LIMIT 1")
    suspend fun get(janCode: String): Goods?

    @Query("SELECT * from goods WHERE goods_code = :productCode  COLLATE NOCASE LIMIT 1")
    suspend fun getByGoodsCode(productCode: String): Goods?

    @Query("SELECT * from goods")
    suspend fun getAll():  List<Goods>
}