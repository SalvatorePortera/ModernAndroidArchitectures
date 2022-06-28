package com.nereus.craftbeer.model.payment

import com.nereus.craftbeer.model.TopUpResult

/**
 * Food shop flow handler
 *
 * @property isPaymentSuccess
 * @property isTopUpSuccess
 * @property paymentResult
 * @property topUpResult
 * コンストラクタ  Food shop flow handler
 */
data class FoodShopFlowHandler(

    var isPaymentSuccess: Boolean = false,

    var isTopUpSuccess: Boolean = false,

    var paymentResult: PaymentResult = FailedPaymentResult(),

    var topUpResult: TopUpResult = TopUpResult.failed()
)





