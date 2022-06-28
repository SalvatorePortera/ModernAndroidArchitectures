package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.model.Pagination
import java.time.LocalDateTime

/**
 * Print receipt by date time model
 *
 * @property startTime
 * @property endTime
 * コンストラクタ  Print receipt by date time model
 */
data class PrintReceiptByDateTimeModel constructor(

    var startTime: LocalDateTime? = null,

    var endTime: LocalDateTime? = null
) : Pagination()





