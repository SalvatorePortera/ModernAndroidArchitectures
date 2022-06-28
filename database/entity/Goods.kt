package com.nereus.craftbeer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nereus.craftbeer.constant.DEFAULT_REDUCTION_TAX_RATE
import com.nereus.craftbeer.constant.DEFAULT_TAX_RATE
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.model.CombinationGoodsInfo

/**
 * Goods
 *
 * @property goodsId
 * @property goodsCode
 * @property janCode
 * @property type
 * @property goodsName
 * @property goodsShortName
 * @property description
 * @property soldOutFlag
 * @property handlingFlag
 * @property purchaseCost
 * @property imageKey
 * @property imageUrl
 * @property sellingPrice
 * @property taxReduction
 * @property taxRate
 * コンストラクタ  Goods
 */
@Entity(tableName = "goods")
data class Goods(
    @ColumnInfo(name = "goods_id")
    val goodsId: String,

    @ColumnInfo(name = "goods_code")
    val goodsCode: String,

    @ColumnInfo(name = "jan_code")
    val janCode: String,

    val type: Short,

    @ColumnInfo(name = "goods_name")
    val goodsName: String,

    @ColumnInfo(name = "goods_short_name")
    val goodsShortName: String,

    val description: String,

    @ColumnInfo(name = "sold_out_flag")
    val soldOutFlag: Short,

    @ColumnInfo(name = "handling_flag")
    val handlingFlag: Short,

    @ColumnInfo(name = "purchase_cost")
    val purchaseCost: Double,

    @ColumnInfo(name = "image_key")
    val imageKey: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "selling_price")
    val sellingPrice: Double,

    @ColumnInfo(name = "tax_reduction")
    val taxReduction: Int,

    @ColumnInfo(name = "tax_rate")
    val taxRate: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

fun Goods.asCombinationGoodsInfo(): CombinationGoodsInfo {
    val cFood = CombinationGoodsInfo(id = id.toString(), janCode = janCode)

    cFood.goodsCode = goodsCode
    cFood.goodsShortName = goodsShortName
    cFood.goodsName = goodsName
    cFood.quantity = 1
    cFood.handlingFlag = handlingFlag.toInt()
    cFood.imageUrl =  imageUrl
    cFood.imageKey = imageKey
    cFood.sellingPrice = sellingPrice
    cFood.soldOutFlag = soldOutFlag.toInt()
    cFood.taxReduction = taxReduction
    cFood.taxRate = taxRate
    return cFood
}
