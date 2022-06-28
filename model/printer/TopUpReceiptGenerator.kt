package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toThousandSeparatorString

/**
 * Top up receipt generator
 *
 * @property receipt
 * コンストラクタ  Top up receipt generator
 */
class TopUpReceiptGenerator(val receipt: TopUpReceipt) : ReceiptGenerator(receipt = receipt) {

    /**
     * Get canvas height
     *
     * @return
     */
    override fun getCanvasHeight(): Int {
        return TOP_UP_RECEIPT_HEIGHT_DEFAULT
    }

    /**
     * Build body
     *
     * @param takeAway
     * @return
     */
    override fun buildBody(takeAway: Boolean): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(PdfLine().apply {
                addContent(content = getStringResource(R.string.charge))
                addContent(content = receipt.amount.toThousandSeparatorString(AT_SIGN))
                addContent(content = String.format(FORMATTED_SUB_QUANTITY, 1))
                addContent(content = receipt.amount.toThousandSeparatorString())
            }
            )
            add(PdfLine().apply { addContent(content = SEPARATOR) })
        }
    }
}