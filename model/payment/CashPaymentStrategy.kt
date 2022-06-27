package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.enums.PaymentMethod

/**
 * Cash payment strategy
 *
 * @constructor  Cash payment strategy
 */
class CashPaymentStrategy : PaymentStrategy {

    /**
     * Pay
     *
     * @param payment
     * @return
     */
    override suspend fun pay(payment: Payment): PaymentResult {
        val cashPayment = payment as CashPayment
        val balance = cashPayment.receivedCash - payment.total
        return PaymentResult(isSuccess = balance >= 0, balanceAfter = balance, balanceBefore = cashPayment.receivedCash, paymentMethod = PaymentMethod.PAYMENT_CASH)
    }

    /**
     * Get balance
     *
     * @param payment
     * @return
     */
    override suspend fun getBalance(payment: Payment): Int {
        return 0
    }
}

/**
 * Cash payment
 *
 * @property receivedCash
 * @constructor  Cash payment
 */
data class CashPayment(
    var receivedCash: Int
) : Payment()

