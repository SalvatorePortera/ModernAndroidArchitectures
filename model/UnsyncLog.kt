package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.EMPTY_STRING

import com.nereus.craftbeer.database.entity.SaleLog
import com.nereus.craftbeer.database.entity.TopUp
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Unsync log
 *
 * @property receiptCode
 * @property totalAmount
 * @property productType
 * @property totalPrice
 * @property createdAt
 * @property logName
 * @property saleLog
 * @property topUp
 * コンストラクタ  Unsync log
 */
data class UnsyncLog constructor(

    val receiptCode: String = EMPTY_STRING,

    var totalAmount: Int,

    val productType: Short = ProducType.TAP_BEER.getValue(),

    var totalPrice: Double? = null,

    var createdAt: LocalDateTime,

    val logName: String = EMPTY_STRING,

    @Transient
    var saleLog: SaleLog? = null,

    @Transient
    var topUp: TopUp? = null
) {
    fun getAmountString(): String {
        val formatRes = ProducType.getByValue(productType)?.formatedAmount()
        return if (formatRes != null) {
            getStringResource(formatRes).format(
                totalAmount
            )
        } else {
            totalAmount?.toString() ?: EMPTY_STRING
        }
    }
}

fun List<UnsyncLog>.asSaleLogRequests(): List<SaleLogCreateRequest> {
    return filter { it.saleLog != null }.map { it.saleLog!! }.map {
        SaleLogCreateRequest(
            pointPlusId = it.pointPlusId,
            balanceBefore = it.balanceBefore,
            balanceAfter = it.balanceAfter,
            createdAt = it.createdAt.toISOString(),
            paymentMethod = it.paymentMethod,
            totalSellingPrice = it.totalSellingPrice.toString(),
            totalTax = it.tax.toString(),
            saleLog = it,
            takeAway = it.takeAway,
            receiptCode = it.receiptCode
        )
    }
}

fun List<UnsyncLog>.asTopUpRequests(): List<TopUpCreateRequest> {
    return filter { it.topUp != null }.map { it.topUp!! }.map {
        TopUpCreateRequest(
            pointPlusId = it.pointPlusId,
            balanceBefore = it.balanceBefore,
            balanceAfter = it.balanceAfter,
            deposit = it.deposit,
            change = it.change,
            paymentMethod = it.paymentMethod,
            createdAt = it.createdAt.toISOString(),
            receiptCode = it.receiptCode,
            amount = it.amount,
            topUp = it
        )

    }
}



