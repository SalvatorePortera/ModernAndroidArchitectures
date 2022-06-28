package com.nereus.craftbeer.model

/**
 * Goods input
 *
 * @property barcode
 * @property productCd
 * @property sellingPrice
 * コンストラクタ  Goods input
 */
data class GoodsInput(

    var barcode: String? = null,

    var productCd: String? = null,

    var sellingPrice: Double = 0.0
)





