package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.enums.PaymentMethod

class CashPaymentStrategy : PaymentStrategy {
    override suspend fun pay(payment: Payment): PaymentResult {
        val cashPayment = payment as CashPayment
        val balance = cashPayment.receivedCash - payment.total
        return PaymentResult(isSuccess = balance >= 0, balanceAfter = balance, balanceBefore = cashPayment.receivedCash, paymentMethod = PaymentMethod.PAYMENT_CASH)
    }

    override suspend fun getBalance(payment: Payment): Int {
        return 0
    }
}

data class CashPayment(
    var receivedCash: Int
) : Payment()

