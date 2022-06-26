package com.nereus.craftbeer.model.payment

data class BeerShopFlowHandler(

    var isPaymentSuccess: Boolean = false,

    var shouldPrintReceipt: Boolean = false,

    var isPouringFinished: Boolean = false,

    var isExecuted: Boolean = false,

    var isBeerPouringCorrectionDone: Boolean = false,

    var paymentResult: PaymentResult = FailedPaymentResult()
)





