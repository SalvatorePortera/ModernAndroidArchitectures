package com.nereus.craftbeer.model

import com.nereus.craftbeer.database.entity.SaleLog

data class SaleLogCreateRequest(

    val pointPlusId: String,

    val balanceBefore: Int = 0,

    val balanceAfter: Int = 0,

    var receiptCode: String,

    val paymentMethod: Short,

    val totalSellingPrice: String,

    val totalTax: String,

    var saleLogDetails: List<SaleLogDetail>? = null,

    val createdAt: String,

    @Transient var saleLog: SaleLog? = null,

    val takeAway: Short
) {

    data class Goods(
        val id: String
    )

    data class TapBeer(
        val id: String
    )

    data class SaleLogDetail(
        val goods: Goods?,
        val tapBeer: TapBeer?,
        val sellingPrice: String,
        val purchaseCost: String,
        val tax: String,
        val taxRate: Double,
        val amount: Int
    )

}


