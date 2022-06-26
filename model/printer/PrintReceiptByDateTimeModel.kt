package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.model.Pagination
import java.time.LocalDateTime

data class PrintReceiptByDateTimeModel constructor(

    var startTime: LocalDateTime? = null,

    var endTime: LocalDateTime? = null
) : Pagination()





