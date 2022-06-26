package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.util.formatStringWithIndent
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toThousandSeparatorString
import kotlin.math.roundToInt
import timber.log.Timber

class SaleReceiptGenerator(val receipt: SaleReceipt) : ReceiptGenerator(receipt = receipt) {
    override fun getCanvasHeight(): Int {
        return SALE_RECEIPT_HEIGHT_DEFAULT + receipt.goods.size * RECEIPT_LINE_SPACE
    }

    override fun buildBody(takeAway: Boolean): List<PdfLine> {
        return ArrayList<PdfLine>().let { lines ->
            lines.addAll(receipt.goods.map { item -> item.buildGoodsUnitLine(takeAway) })
            lines.add(PdfLine().apply { addContent(content = SEPARATOR) })
            lines
        }
    }

    override fun buildTaxInfo(): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            // Tax 8%
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.receipt_sub_totaL_8)
                )
                addEmptyContent()
                addContent(
                    content = receipt.goods.subReducedTotalPriceWithoutTax(receipt.isTakeAway)
                        .toInt()
                        .toThousandSeparatorString()
                )
            }
            )

            // Comsumption Tax 8%
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.receipt_consumption_tax_8)
                )
                addEmptyContent()
                addContent(
                    content = receipt.goods.subReducedTotalTax(receipt.isTakeAway).roundToInt()
                        .toThousandSeparatorString()
                )
            }
            )

            // Tax 10%
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.receipt_sub_totaL_10)
                )
                addEmptyContent()
                addContent(
                    content = receipt.goods.subFullTotalPriceWithoutTax(receipt.isTakeAway)
                        .roundToInt()
                        .toThousandSeparatorString()
                )
            }
            )

            // Comsumption Tax 10%
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.receipt_consumption_tax_10)
                )
                addEmptyContent()
                addContent(
                    content = receipt.goods.subFullTotalTax(receipt.isTakeAway).roundToInt()
                        .toThousandSeparatorString()
                )
            }
            )
            // Total
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.receipt_totaL)
                )
                addEmptyContent()
                addContent(
                    content = receipt.goods.totalPriceWithTax(receipt.isTakeAway).roundToInt()
                        .toThousandSeparatorString()
                )
            }
            )

            // Quantity
            add(PdfLine().apply {
                addEmptyContent()
                addContent(
                    content = getStringResource(R.string.quantity)
                )
                addEmptyContent()
                addContent(
                    content = String.format(
                        getStringResource(R.string.formatedQuantity),
                        receipt.goods.totalQuantity()
                    )
                )
            }
            )
        }

    }

    fun CombinationGoodsInfo.buildGoodsUnitLine(takeAway: Boolean): PdfLine {

        return PdfLine().apply {
            val taxPrefix = if (!takeAway && taxReduction == 1) {
                TAX_REDUCTION_PREFIX
            } else EMPTY_STRING

            addContent(
                content = (taxPrefix + goodsShortName).formatStringWithIndent(
                    GOODS_NAME_LIMIT
                )
            )

            addContent(
                content = sellingPriceWithTax(takeAway).roundToInt()
                    .toThousandSeparatorString(AT_SIGN)
            )

            addContent(
                content = String.format(FORMATTED_SUB_QUANTITY, quantity)
                    .formatStringWithIndent(
                        SUB_QUANTITY_LIMIT
                    )
            )
            addContent(
                content = (quantity * sellingPrice * (1 + getTaxRate(takeAway))).roundToInt()
                    .toThousandSeparatorString()
            )
        }
    }
}
