package com.nereus.craftbeer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nereus.craftbeer.database.entity.Goods

@Dao
interface GoodsDao {
    /**
     * Insert multiple
     * Do insert in background (suspend)
     * @param goodsList
     */
    @Insert
    suspend fun insertMultiple(goodsList: List<Goods>)

    /**
     * Insert without coroutine
     * Do insert in mainthread (just For test)
     * @param goods
     */
    @Insert
    fun insertWithoutCoroutine(goods: Goods)

    /**
     * Update
     * Do update in background (suspend)
     * @param goods
     */
    @Update
    suspend fun update(goods: Goods)

    /**
     * Delete all
     * Do update in background (suspend)
     */
    @Query("DELETE FROM goods")
    suspend fun deleteAll()

    /**
     * Get
     *
     * @param janCode
     * @return
     */
    @Query("SELECT * from goods WHERE jan_code = :janCode LIMIT 1")
    suspend fun get(janCode: String): Goods?

    /**
     * Get by goods code
     *
     * @param productCode
     * @return
     */
    @Query("SELECT * from goods WHERE goods_code = :productCode  COLLATE NOCASE LIMIT 1")
    suspend fun getByGoodsCode(productCode: String): Goods?

    /**
     * Get all
     *
     * @return
     */
    @Query("SELECT * from goods")
    suspend fun getAll():  List<Goods>
}