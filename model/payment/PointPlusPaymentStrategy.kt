package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.R
import com.nereus.craftbeer.enums.pointplus.v4.RequestType
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.pointplus.v4.transaction.BasedCardResponseTransaction
import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardRequestTransactions
import com.nereus.craftbeer.model.pointplus.v4.transactions.fillBalanceInquiryRequest
import com.nereus.craftbeer.model.pointplus.v4.transactions.fillPaymentRequest
import com.nereus.craftbeer.repository.PointPlusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.jvm.Throws

class PointPlusPaymentStrategy : PaymentStrategy {
    @Throws(MessageException::class)
    override suspend fun pay(payment: Payment): PaymentResult {
        val ePayment = payment as EMoneyPayment
        val balance = getBalance(payment)
        if (!ePayment.isValidBalance(balance)) {
            throw MessageException(MessagesModel(R.string.msg_not_enough_balance, balance))
        }
        val response = payPointPlus(ePayment)
        return PaymentResult(balanceAfter = response.newValue, balanceBefore = response.oldValue)
    }

    override suspend fun getBalance(payment: Payment): Int {
        val payment = payment as EMoneyPayment
        val transactions = BasedCardRequestTransactions(RequestType.QUERY_BALANCE)
        transactions.fillBalanceInquiryRequest(payment.pointPlusId, payment.cardAuthInfo)
        val response = withContext(Dispatchers.IO) {
            payment.repository.callCardApi(transactions)
        }
        Timber.d("balance is" + response.newValue)
        return response.newValue
    }

    private fun EMoneyPayment.isValidBalance(balance: Int): Boolean {
        return balance >= total
    }

    private suspend fun payPointPlus(payment: EMoneyPayment): BasedCardResponseTransaction {
        val transactions = BasedCardRequestTransactions(RequestType.VALUE_PAYMENT)
        transactions.fillPaymentRequest(
            payment.pointPlusId,
            payment.total,
            payment.receiptCode,
            payment.cardAuthInfo
        )
        val response = withContext(Dispatchers.IO) {
            payment.repository.callCardApi(transactions)
        }
        Timber.d("new balance is" + response.newValue)
        return response
    }
}

data class EMoneyPayment(
    var pointPlusId: String,
    var cardAuthInfo: String,
    var repository: PointPlusRepository,
    var receivedAmount: Double? = null
) : Payment()