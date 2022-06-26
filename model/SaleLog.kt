package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.model.printer.SaleReceipt
import java.time.LocalDateTime

/**
 * Sale log
 *
 * @property id
 * @property pointPlusId
 * @property balanceBefore
 * @property balanceAfter
 * @property companyId
 * @property shopId
 * @property paymentMethod
 * @property takeAway
 * @property countPrinted
 * @property isIssued
 * @property totalSellingPrice
 * @property purchaseCost
 * @property tax
 * @property createdAt
 * @property receiptCode
 * @property saleLogDetails
 * @constructor Create empty Sale log
 */
data class SaleLog constructor(

    var id: String,

    var pointPlusId: String,

    var balanceBefore: Int = 0,

    var balanceAfter: Int = 0,

    var companyId: String,

    var shopId: String,

    var paymentMethod: Short,

    var takeAway: Short,

    var countPrinted: Short = 0,

    var isIssued: Short = 0,

    var totalSellingPrice: Double,

    var purchaseCost: Double,

    var tax: Double,

    var createdAt: LocalDateTime,

    var receiptCode: String? = EMPTY_STRING,

    var saleLogDetails: List<SaleLogDetail>
) {
    fun getProductType(): ProducType {
        return if (saleLogDetails.first().goodsId.isNullOrBlank()) {
            ProducType.TAP_BEER
        } else ProducType.GOODS
    }
}

data class SaleLogData(
    val data: List<SaleLog>
)

fun List<SaleLog>.asReceipts(): List<Receipt> {
    return map { SaleReceipt(it) }
}





