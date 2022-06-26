package com.nereus.craftbeer.model.payment

/**
 * Beer shop flow handler
 *
 * @property isPaymentSuccess
 * @property shouldPrintReceipt
 * @property isPouringFinished
 * @property isExecuted
 * @property isBeerPouringCorrectionDone
 * @property paymentResult
 * @constructor Create empty Beer shop flow handler
 */
data class BeerShopFlowHandler(

    var isPaymentSuccess: Boolean = false,

    var shouldPrintReceipt: Boolean = false,

    var isPouringFinished: Boolean = false,

    var isExecuted: Boolean = false,

    var isBeerPouringCorrectionDone: Boolean = false,

    var paymentResult: PaymentResult = FailedPaymentResult()
)





