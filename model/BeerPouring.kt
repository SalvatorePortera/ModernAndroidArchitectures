package com.nereus.craftbeer.model

/**
 * Beer pouring
 *
 * @property balance
 * @property total
 * @property tax
 * @property amountInMl
 * @property sellingPrice
 * @constructor Create empty Beer pouring
 */
data class BeerPouring(

    var balance: Int = 0,//残高

    var total: Double = 0.0,//合計

    var tax: Double = 0.0,//税率

    var amountInMl: Int = 0,//量(ml)

    var sellingPrice: Double = 0.0//売価
)





