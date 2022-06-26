package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.SEPARATOR
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toThousandSeparatorString

class TopUpCashReceiptPayment(receipt: Receipt) :
    ReceiptPayment(receipt = receipt) {
    override fun build(): List<PdfLine> {
        val topUpReceipt = receipt as TopUpReceipt
        return ArrayList<PdfLine>().apply {
            add(buildPaymentMethodLine(receipt.paymentMethod!!))
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.cash_deposit)
                )
                addEmptyContent()
                addContent(
                    content = topUpReceipt.deposit.toThousandSeparatorString() // TODO get before/after
                )
            })
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.cash_change)
                )
                addEmptyContent()
                addContent(
                    content = topUpReceipt.change.toThousandSeparatorString() // TODO get before/after
                )
            })
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
                    content = receipt.balanceBefore.toThousandSeparatorString()
                )
            })
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.point_plus_balance_after)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceAfter.toThousandSeparatorString()
                )
            })
        }
    }
}

class TopUpDefaultReceiptPayment(receipt: Receipt) :
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
                    content = receipt.balanceBefore.toThousandSeparatorString()
                )
            })
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.point_plus_balance_after)
                )
                addEmptyContent()
                addContent(
                    content = receipt.balanceAfter.toThousandSeparatorString()
                )
            })
        }
    }
}

