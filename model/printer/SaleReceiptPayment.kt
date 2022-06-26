package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.SEPARATOR
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toThousandSeparatorString

class PointPlusReceiptPayment(receipt: Receipt) :
    ReceiptPayment(receipt = receipt) {
    override fun build(): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(buildPaymentMethodLine(receipt.paymentMethod!!))
            add(PdfLine().apply { addContent(content = SEPARATOR) })
            add(PdfLine().apply { addContent(content = getStringResource(R.string.point_plus_payment_title)) })
            if (!receipt.isFirstPrinting) {
                add(PdfLine().apply {
                    addContent(content = getStringResource(R.string.reprinting_note))
                }
                )
            }
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.point_plus_balance_before)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceBefore.toThousandSeparatorString() // TODO get before/after
                )
            })
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.point_plus_balance_after)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceAfter.toThousandSeparatorString() // TODO get before/after
                )
            })
        }
    }
}

class CashReceiptPayment(receipt: Receipt) :
    ReceiptPayment(receipt = receipt) {
    override fun build(): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(buildPaymentMethodLine(receipt.paymentMethod!!))
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.cash_deposit)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceBefore.toThousandSeparatorString()
                )
            })
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.cash_change)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceAfter.toThousandSeparatorString()
                )
            })
        }
    }
}

class DefaultReceiptPayment(receipt: Receipt) :
    ReceiptPayment(receipt = receipt) {
    override fun build(): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(buildPaymentMethodLine(receipt.paymentMethod!!))
        }
    }
}

