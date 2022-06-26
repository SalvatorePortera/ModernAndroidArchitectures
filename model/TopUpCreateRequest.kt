package com.nereus.craftbeer.model

import androidx.room.ColumnInfo
import com.nereus.craftbeer.database.entity.TopUp

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

