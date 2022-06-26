package com.nereus.craftbeer.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.model.printer.TopUpReceipt
import com.nereus.craftbeer.util.getStringResource
import java.time.LocalDateTime

/**
 * Top up
 *
 * @property id
 * @property pointPlusId
 * @property cardAuthInfo
 * @property receiptCode
 * @property companyId
 * @property shopId
 * @property deposit
 * @property change
 * @property amount
 * @property countPrinted
 * @property isIssued
 * @property balanceBefore
 * @property balanceAfter
 * @property paymentMethod
 * @property createdAt
 * @constructor Create empty Top up
 */
data class TopUp(

    var id: String = EMPTY_STRING,

    var pointPlusId: String = EMPTY_STRING,

    var cardAuthInfo: String = EMPTY_STRING,

    var receiptCode: String? = EMPTY_STRING,

    var companyId: String = EMPTY_STRING,

    var shopId: String = EMPTY_STRING,

    var deposit: Int = 0, // For Cash only

    var change: Int = 0, // For Cash only

    var amount: Int = 0,

    var countPrinted: Short = 0,

    var isIssued: Short = 0,

    val balanceBefore: Int = 0,

    val balanceAfter: Int = 0,

    var paymentMethod: Short? = PaymentMethod.PAYMENT_HOUSE_MONEY.getValue(),

    var createdAt: LocalDateTime? = null
) {
    fun buildTopUpName(): String {
        return getStringResource(R.string.formatedTopUpAmount).format(amount)
    }
}

data class TopUpData(
    val data: List<TopUp>
)

data class TopUpResult(
    // Balance after payment
    var isSuccess: Boolean = true,

    val amount: Int = 0,

    val balanceBefore: Int = 0,

    val balanceAfter: Int = 0
) {
    companion object {
        fun failed(): TopUpResult {
            return TopUpResult(isSuccess = false)
        }
    }
}

fun List<TopUp>.asReceipts(): List<Receipt> {
    return map { TopUpReceipt(it) }
}







