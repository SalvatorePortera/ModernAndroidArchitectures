package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.constant.EMPTY_STRING
import com.seikoinstruments.sdk.thermalprinter.printerenum.*

data class PrinterResponse(

    var code: String = EMPTY_STRING,

    var message: String = EMPTY_STRING,

    var receiptId: String = EMPTY_STRING,

    var isMultiplePrinting: Boolean = false,

    var isIssued: Boolean = false,

    var isTopUp: Boolean = false,
)





