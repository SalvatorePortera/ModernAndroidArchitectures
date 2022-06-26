package com.nereus.craftbeer.model.pointplus.v4.transactions

import com.nereus.craftbeer.enums.pointplus.v4.RequestType
import com.nereus.craftbeer.model.pointplus.v4.transaction.BasedCardRequestTransaction
import com.nereus.craftbeer.util.genRandomString
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "transactions", strict = false)
class BasedCardRequestTransactions(requestType: RequestType) : RequestTransactions() {

    @field:Element(name = "transaction")
    var transaction = BasedCardRequestTransaction(requestType)
}

fun BasedCardRequestTransactions.fillBalanceInquiryRequest(poinPlusId: String,
                                                           cardAuthInfo: String) {
    transaction.apply {
        requestId = genRandomString(6)
        this.cardAuthInfo = cardAuthInfo
        // required for BalanceInquiry
        memberCode = poinPlusId
    }
}

fun BasedCardRequestTransactions.fillPaymentRequest(
    poinPlusId: String,
    amount: Int,
    receiptCode: String,
    cardAuthInfo: String
) {
    transaction.apply {
        requestId = receiptCode
        this.cardAuthInfo = cardAuthInfo
        // required for Payment Value
        memberCode = poinPlusId
        inputValue = amount
        posReceiptCode = receiptCode
    }
}

fun BasedCardRequestTransactions.fillChargeValueRequest(
    poinPlusId: String,
    amount: Int,
    receiptCode: String,
    cardAuthInfo: String
) {
    transaction.apply {
        requestId = receiptCode
        this.cardAuthInfo = cardAuthInfo
        // required for charge Value
        memberCode = poinPlusId
        inputValue = amount
        posReceiptCode = receiptCode
    }
}


