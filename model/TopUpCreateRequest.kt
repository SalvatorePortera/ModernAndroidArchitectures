package com.nereus.craftbeer.model

import androidx.room.ColumnInfo
import com.nereus.craftbeer.database.entity.TopUp

/**
 * Top up create request
 *
 * @property pointPlusId
 * @property amount
 * @property deposit
 * @property change
 * @property balanceBefore
 * @property balanceAfter
 * @property receiptCode
 * @property paymentMethod
 * @property createdAt
 * @property topUp
 * コンストラクタ  Top up create request
 */
data class TopUpCreateRequest(

    val pointPlusId: String,

    var amount: Int = 0,

    var deposit: Int = 0,

    var change: Int = 0,

    val balanceBefore: Int,

    val balanceAfter: Int,

    var receiptCode: String,

    val paymentMethod: Short,

    val createdAt: String,

    @Transient var topUp: TopUp? = null
)

