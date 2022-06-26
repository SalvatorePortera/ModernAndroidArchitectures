package com.nereus.craftbeer.database.entity

import androidx.room.*

data class SaleLogWithSaleLogDetail(
    @Embedded val saleLog: SaleLog,
    @Relation(
        parentColumn = "sale_log_id",
        entityColumn = "sale_log_id"
    )
    val saleList: List<SaleLogDetail>
)