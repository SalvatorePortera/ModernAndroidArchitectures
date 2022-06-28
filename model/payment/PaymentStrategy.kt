package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.model.MessagesModel
import java.time.LocalDateTime

/**
 * Payment strategy
 *
 * コンストラクタ  Payment strategy
 */
interface PaymentStrategy {
    suspend fun pay(payment: Payment) : PaymentResult

    suspend fun getBalance(payment: Payment) : Int
}

/**
 * Payment
 *
 * コンストラクタ  Payment
 */
abstract class Payment {
    var total: Int = 0

    // Balance after payment
    var receiptCode: String = EMPTY_STRING

    var receiptTimestamp: LocalDateTime?= null
}

/**
 * Payment result
 *
 * @property balanceBefore
 * @property balanceAfter
 * @property isSuccess
 * @property paymentMethod
 * コンストラクタ  Payment result
 */
open class PaymentResult(
    // Balance after payment
    val balanceBefore: Int = 0,

    val balanceAfter: Int = 0,

    open var isSuccess: Boolean = true,

    var paymentMethod: PaymentMethod = PaymentMethod.PAYMENT_HOUSE_MONEY
)

/**
 * Failed payment result
 *
 * @property isSuccess
 * コンストラクタ  Failed payment result
 */
data class FailedPaymentResult(
    override var isSuccess: Boolean = false
) : PaymentResult()
