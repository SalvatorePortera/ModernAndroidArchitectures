package com.nereus.craftbeer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.model.TopUpCreateRequest
import com.nereus.craftbeer.model.UnsyncLog
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Top up
 *
 * @property pointPlusId
 * @property companyId
 * @property shopId
 * @property amount
 * @property deposit
 * @property change
 * @property balanceBefore
 * @property balanceAfter
 * @property receiptCode
 * @property topUpName
 * @property paymentMethod
 * @property createdAt
 * @constructor Create empty Top up
 */
@Entity(tableName = "top_up")
data class TopUp(

    @ColumnInfo(name = "point_plus_id")
    val pointPlusId: String,

    @ColumnInfo(name = "company_id")
    val companyId: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "amount")
    val amount: Int = 0,

    @ColumnInfo(name = "deposit")
    val deposit: Int = 0,

    @ColumnInfo(name = "change")
    val change: Int = 0,

    @ColumnInfo(name = "balance_before")
    val balanceBefore: Int = 0,

    @ColumnInfo(name = "balance_after")
    val balanceAfter: Int = 0,

    @ColumnInfo(name = "receipt_code")
    val receiptCode: String = EMPTY_STRING,

    @ColumnInfo(name = "top_up_name")
    val topUpName: String = EMPTY_STRING,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: Short,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "top_up_id")
    var id: Long = 0L
}

fun TopUp.asTopUpRequest(): TopUpCreateRequest {
    return TopUpCreateRequest(
        pointPlusId = pointPlusId,
        deposit = deposit,
        change = change,
        balanceBefore = balanceBefore,
        balanceAfter = balanceAfter,
        paymentMethod = paymentMethod,
        amount = amount,
        receiptCode = receiptCode,
        createdAt = createdAt.toISOString()
    )
}


fun List<TopUp>.asUnsyncLogs(): List<UnsyncLog> {
    return map {
        UnsyncLog(
            createdAt = it.createdAt,
            receiptCode = it.receiptCode,
            totalAmount = it.amount,
            productType = ProducType.TOP_UP.getValue(),
            logName = it.topUpName,
            topUp = it
        )
    }
}
