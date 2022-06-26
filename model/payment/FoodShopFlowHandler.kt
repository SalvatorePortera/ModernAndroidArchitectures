package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.model.TopUpResult

data class FoodShopFlowHandler(

    var isPaymentSuccess: Boolean = false,

    var isTopUpSuccess: Boolean = false,

    var paymentResult: PaymentResult = FailedPaymentResult(),

    var topUpResult: TopUpResult = TopUpResult.failed()
)





