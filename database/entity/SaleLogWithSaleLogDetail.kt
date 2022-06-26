package com.nereus.craftbeer.database.entity

import androidx.room.*

/**
 * Sale log with sale log detail
 *
 * @property saleLog
 * @property saleList
 * @constructor Create empty Sale log with sale log detail
 */
data class SaleLogWithSaleLogDetail(
    @Embedded val saleLog: SaleLog,
    @Relation(
        parentColumn = "sale_log_id",
        entityColumn = "sale_log_id"
    )
    val saleList: List<SaleLogDetail>
)