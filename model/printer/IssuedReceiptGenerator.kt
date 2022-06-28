package com.nereus.craftbeer.model.printer

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.printer.XAxisPosition
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.util.buildPdfDocument
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toReceiptDateTimeString
import com.nereus.craftbeer.util.toThousandSeparatorString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Issued receipt generator
 *
 * @property receipt
 * コンストラクタ  Issued receipt generator
 */
open class IssuedReceiptGenerator(private val receipt: Receipt) {

    /**
     * Generate
     *
     * @return
     */
    suspend fun generate(): PdfDocument {
        val printModels1 = withContext(Dispatchers.Default) {
            buildPdfLines()
        }

        Timber.i(
            "------------- generate issued receipt %s", receipt.receiptCode
        )

        return buildPdfDocument(printModels1, getCanvasHeight())
    }

    /**
     * Build body
     *
     * @param takeAway
     * @return
     */
    protected open fun buildBody(takeAway: Boolean = true): List<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(PdfLine().apply {
                addContent(
                    content = receipt.getTotal().roundToInt()
                        .toThousandSeparatorString(suffix = MINUS_SIGN),
                    yOffset = 2 * RECEIPT_GRID_SIZE,
                    alignment = Paint.Align.CENTER,
                    startPos = XAxisPosition.CENTER,
                    textSize = RECEIPT_TEXT_SIZE_XLARGE
                )
            })
        }
    }

    protected open fun getDefaultTextSize(): Float {
        return RECEIPT_TEXT_SIZE_LARGE
    }

    private fun buildPdfLines(): List<PdfLine> {
        val shop = receipt.shop!!
        val company = receipt.company!!
        return ArrayList<PdfLine>().apply {
            addAll(buildHeader())
            addAll(buildBody())
            addAll(buildFooter(company, shop))
        }
    }

    protected open fun getCanvasHeight(): Int {
        return ISSUED_RECEIPT_HEIGHT_DEFAULT
    }

    private fun buildHeader(): List<PdfLine> {
        return ArrayList<PdfLine>().apply {

            add(PdfLine().apply {
                addContent(
                    content = getStringResource(R.string.receipt),
                    yOffset = RECEIPT_GRID_SIZE,
                    alignment = Paint.Align.CENTER,
                    startPos = XAxisPosition.CENTER,
                    textSize = RECEIPT_TEXT_SIZE_XLARGE
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = getStringResource(R.string.sama),
                    yOffset = 3 * RECEIPT_GRID_SIZE,
                    alignment = Paint.Align.RIGHT,
                    startPos = XAxisPosition.BETWEEN_3_AND_4,
                    textSize = getDefaultTextSize()
                )
            })
            add(PdfLine().apply {
                addContent(
                    content = SEPARATOR,
                    startPos = XAxisPosition.COLUMN_1,
                    endPos = XAxisPosition.BETWEEN_3_AND_4
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = receipt.createdAt!!.toReceiptDateTimeString(),
                    yOffset = RECEIPT_GRID_SIZE,
                    alignment = Paint.Align.RIGHT,
                    startPos = XAxisPosition.COLUMN_4,
                    textSize = RECEIPT_TEXT_SIZE_DEFAULT
                )
            })
        }
    }

    private fun buildFooter(company: Company, shop: ShopInfo): ArrayList<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(PdfLine().apply {
                addContent(
                    content = SEPARATOR,
                    startPos = XAxisPosition.COLUMN_1,
                    endPos = XAxisPosition.COLUMN_4
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = getStringResource(R.string.issued_receipt_note_1),
                    yOffset = RECEIPT_GRID_SIZE,
                    alignment = Paint.Align.LEFT,
                    startPos = XAxisPosition.COLUMN_1,
                    textSize = getDefaultTextSize()
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = SEPARATOR,
                    startPos = XAxisPosition.COLUMN_1,
                    endPos = XAxisPosition.COLUMN_4
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = getStringResource(R.string.issued_receipt_note_2),
                    alignment = Paint.Align.LEFT,
                    startPos = XAxisPosition.COLUMN_1,
                    textSize = getDefaultTextSize()
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = getStringResource(R.string.issued_income_stamp),
                    lettersPerLine = 4,
                    isFramed = true,
                    yOffset = RECEIPT_GRID_SIZE,
                    textSize = RECEIPT_TEXT_SIZE_XLARGE,
                    startPos = XAxisPosition.COLUMN_1
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = company.companyName,
                    alignment = Paint.Align.RIGHT,
                    startPos = XAxisPosition.COLUMN_4,
                    yOffset = RECEIPT_GRID_SIZE,
                    textSize = getDefaultTextSize(),
                    canMultiLines = true
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = shop.address,
                    alignment = Paint.Align.RIGHT,
                    startPos = XAxisPosition.COLUMN_4,
                    yOffset = RECEIPT_GRID_SIZE
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = shop.phoneNumber,
                    alignment = Paint.Align.RIGHT,
                    startPos = XAxisPosition.COLUMN_4,
                    yOffset = RECEIPT_GRID_SIZE
                )
            })
        }
    }
}

