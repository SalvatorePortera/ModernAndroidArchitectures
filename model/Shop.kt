package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.EMPTY_STRING

/**
 * Shop
 *
 * @property companyName
 * @property shopName
 * @property shopId
 * @property companyId
 * @property phone
 * @property postalCode
 * @property address
 * @constructor Create empty Shop
 */
data class Shop(

    var companyName: String = EMPTY_STRING,

    var shopName: String = EMPTY_STRING,

    var shopId: String = EMPTY_STRING,

    var companyId: String = EMPTY_STRING,

    var phone: String = EMPTY_STRING,

    var postalCode: String = EMPTY_STRING,

    var address: String = EMPTY_STRING
)





