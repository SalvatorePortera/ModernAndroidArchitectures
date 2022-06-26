package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.R
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.util.getStringResource

abstract class ReceiptPayment(val receipt: Receipt) {
    abstract fun build(): List<PdfLine>

    protected fun buildPaymentMethodLine(paymentMethod: PaymentMethod): PdfLine {
        return PdfLine().apply {
            addEmptyContent()
            addContent(
                content = getStringResource(R.string.payment_method)
            )
            addEmptyContent()
            addContent(
                content = paymentMethod.getName()
            )
        }
    }
}

