package com.nereus.craftbeer.model.pointplus.v4.transactions

import com.nereus.craftbeer.enums.pointplus.v4.RequestType
import com.nereus.craftbeer.model.pointplus.v4.transaction.BasedCardResponseTransaction
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root


/**
 * Based card response transactions
 *
 * @constructor  Based card response transactions
 */
@Root(name = "transactions", strict = false)
class BasedCardResponseTransactions() : ResponseTransactions() {

    @field:Element(name = "transaction")
    var transaction = BasedCardResponseTransaction()
}

/**
 * Fill balance inquiry response
 *
 */
fun BasedCardResponseTransactions.fillBalanceInquiryResponse() {
    transaction.apply {
        requestType = RequestType.QUERY_BALANCE.getValue()
        requestId = "125645566"
        clientSignature = "九九九　ﾁﾁﾁ  チチチ"
        transactionType = "125645566"
        retryCount = 4
        terminalDate = "20201206"
        terminalTime = "120232"

        /*common res*/
        authYmd = "20201206"
        authHms = "120232"
        messageLogId = "2"
        errorCode = "125645566"
        message1 = "125645566"
        message2 = "125645566"

        // required for BalanceInquiry
        memberCode = "125645566"
        cardAuthType = 1
        cardAuthInfo = "125645566"
        defaultService = 2

        //balance inquiry res
        newValue = 2
        newChargeValueBalance = 23
        newPoint = 1
        expireYmd = "20201206"
    }
}


