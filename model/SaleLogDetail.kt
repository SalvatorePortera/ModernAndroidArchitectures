package com.nereus.craftbeer.model

import com.nereus.craftbeer.R
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

data class SaleLogDetail constructor(

    var id: String,

    var saleLogId: String,

    var goodsId: String?,

    var goodsData: GoodsShop?,

    var tapBeerId: String?,

    var tapBeerData: TapBeer?,

    var amount: Int,

    var purchaseCost: Double,

    var sellingPrice: Double
)

fun List<SaleLogDetail>.asGoodsList(): List<CombinationGoodsInfo> {
    return map {
        CombinationGoodsInfo(
            // goodsCode = it.goodsId.toString(), // 商品コードやJANコードではなく、goods idが取得できる
            // goodsCode = it.goodsData?.goodsCode ?: "", // shopDataには商品コードやJANコードがないので取得できない
            quantity = it.amount,
            sellingPrice = it.sellingPrice,
            goodsShortName = it.goodsData?.goodsShortName ?: it.tapBeerData?.beerShortName
            ?: getStringResource(R.string.unknown_product),
            type = if (it.tapBeerId != null) ProducType.TAP_BEER else ProducType.GOODS
        )
    }
}






