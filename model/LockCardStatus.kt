package com.nereus.craftbeer.model

/**
 * Lock card status
 *
 * @property id
 * @property pointPlusId
 * @property lastUpdateDeviceId
 * @property isLocked
 * @constructor Create empty Lock card status
 */
data class LockCardStatus constructor(

    var id: String? = null,

    var pointPlusId: String,

    var lastUpdateDeviceId: String,

    var isLocked: Short
)






