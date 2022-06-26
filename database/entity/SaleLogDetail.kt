package com.nereus.craftbeer.database.entity

import androidx.lifecycle.Transformations.map
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.nereus.craftbeer.model.SaleLogCreateRequest

@Entity(tableName = "sale_logs_detail")
data class SaleLogDetail(
    @ColumnInfo(name = "sale_log_id")
    @ForeignKey
        (
        entity = SaleLog::class,
        parentColumns = ["sale_log_id"],
        childColumns = ["sale_log_id"],
        onDelete = CASCADE
    )
    val saleLogId: Long,

    @ColumnInfo(name = "goods_id")
    val goodsId: String?,

    @ColumnInfo(name = "tap_beer_id")
    val tapBeerId: String?,

    @ColumnInfo(name = "amount")
    val amount: Int,

    @ColumnInfo(name = "tax_rate")
    val taxRate: Double,

    @ColumnInfo(name = "purchase_cost")
    val purchaseCost: Double,

    @ColumnInfo(name = "selling_price")
    val sellingPrice: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

fun List<SaleLogDetail>.asSaleLogDetailsRequestModel(): List<SaleLogCreateRequest.SaleLogDetail> {
    return map {
        SaleLogCreateRequest.SaleLogDetail(
            goods = if (it.goodsId.isNullOrBlank()) {
                null
            } else SaleLogCreateRequest.Goods(it.goodsId),
            tapBeer = if (it.tapBeerId.isNullOrBlank()) {
                null
            } else SaleLogCreateRequest.TapBeer(it.tapBeerId),
            sellingPrice = it.sellingPrice.toString(),
            purchaseCost = it.purchaseCost.toString(),
            taxRate = it.taxRate,
            amount = it.amount,
            tax = "0"
        )
    }
}

fun List<SaleLogDetail>.asSaleLogDetailsRequestModelBeer(): List<SaleLogCreateRequest.SaleLogDetail> {
    return map {
        SaleLogCreateRequest.SaleLogDetail(
            goods = if (it.goodsId.isNullOrBlank()) {
                null
            } else SaleLogCreateRequest.Goods(it.goodsId),
            tapBeer = if (it.tapBeerId.isNullOrBlank()) {
                null
            } else SaleLogCreateRequest.TapBeer(it.tapBeerId),
            sellingPrice = it.sellingPrice.toString(),
            purchaseCost = it.purchaseCost.toString(),
            taxRate = it.taxRate,
            amount = it.amount,
            tax = (it.taxRate * it.amount * it.sellingPrice).toString()
        )
    }
}

