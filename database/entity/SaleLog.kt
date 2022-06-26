package com.nereus.craftbeer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.model.SaleLogCreateRequest
import com.nereus.craftbeer.model.UnsyncLog
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Sale log
 *
 * @property pointPlusId
 * @property companyId
 * @property shopId
 * @property balanceBefore
 * @property balanceAfter
 * @property receiptCode
 * @property saleLogName
 * @property paymentMethod
 * @property takeAway
 * @property totalSellingPrice
 * @property totalAmount
 * @property tax
 * @property productType
 * @property createdAt
 * @constructor Create empty Sale log
 */
@Entity(tableName = "sale_logs")
data class SaleLog(

    @ColumnInfo(name = "point_plus_id")
    val pointPlusId: String,

    @ColumnInfo(name = "company_id")
    val companyId: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "balance_before")
    val balanceBefore: Int = 0,

    @ColumnInfo(name = "balance_after")
    val balanceAfter: Int = 0,

    @ColumnInfo(name = "receipt_code")
    val receiptCode: String = EMPTY_STRING,

    @ColumnInfo(name = "sale_log_name")
    val saleLogName: String = EMPTY_STRING,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: Short,

    @ColumnInfo(name = "take_away")
    val takeAway: Short,

    @ColumnInfo(name = "total_selling_price")
    val totalSellingPrice: Double,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Int,

    @ColumnInfo(name = "tax")
    val tax: Double,

    @ColumnInfo(name = "type")
    val productType: Short = ProducType.TAP_BEER.getValue(),

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sale_log_id")
    var saleLogId: Long = 0L

    fun getAmountString(): String {
        return if (ProducType.getByValue(productType) == ProducType.TAP_BEER) {
            getStringResource(R.string.formatedMililiter).format(totalAmount)
        } else getStringResource(R.string.formatedQuantity).format(totalAmount)
    }
}

fun SaleLog.asSaleLogRequest(): SaleLogCreateRequest {
    return SaleLogCreateRequest(
        pointPlusId = pointPlusId,
        balanceBefore = balanceBefore,
        balanceAfter = balanceAfter,
        receiptCode = receiptCode,
        paymentMethod = paymentMethod,
        totalSellingPrice = totalSellingPrice.toString(),
        totalTax = tax.toString(),
        takeAway = takeAway,
        createdAt = createdAt.toISOString(),
        saleLog = this
    )
}

fun List<SaleLog>.asUnsyncLogs(): List<UnsyncLog> {
    return map {
        UnsyncLog(
            createdAt = it.createdAt,
            receiptCode = it.receiptCode,
            productType = it.productType,
            totalAmount = it.totalAmount,
            totalPrice = it.totalSellingPrice,
            logName = it.saleLogName,
            saleLog = it
        )
    }
}
