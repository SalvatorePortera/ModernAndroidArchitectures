package com.nereus.craftbeer.model.printer

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.printer.XAxisPosition
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.util.buildPdfDocument
import com.nereus.craftbeer.util.getHeightFromBuildPdfDocument
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toReceiptDateTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Receipt generator
 *
 * @property receipt
 * @constructor  Receipt generator
 */
abstract class ReceiptGenerator(private val receipt: Receipt) {

    suspend fun generate(): PdfDocument {
        val printModels1 = withContext(Dispatchers.Default) {
            buildPdfLines()
        }

        Timber.i(
            "------------- generate receipt %s", receipt.receiptCode
        )

        //return buildPdfDocument(printModels1, getCanvasHeight())
        val height = getHeightFromBuildPdfDocument(printModels1, 1000)
        return buildPdfDocument(printModels1, height)
    }

    protected fun buildPayment(): List<PdfLine> {
        return receipt.getReceiptPayment().build()
    }

    protected open fun buildBody(takeAway: Boolean = true): List<PdfLine> {
        return ArrayList()
    }

    protected open fun buildTaxInfo(): List<PdfLine> {
        return ArrayList()
    }

    private fun buildPdfLines(): List<PdfLine> {
        val shop = receipt.shop!!
        val company = receipt.company!!
        return ArrayList<PdfLine>().apply {
            addAll(buildHeader(shop, company))
            addAll(buildBody())
            addAll(buildTaxInfo())
            addAll(buildPayment())
            addAll(buildFooter(shop))
        }
    }

    protected open fun getCanvasHeight(): Int {
        return SALE_RECEIPT_HEIGHT_DEFAULT
    }

    private fun buildHeader(
        shop: ShopInfo,
        company: Company
    ): List<PdfLine> {
        return ArrayList<PdfLine>().apply {

            add(PdfLine().apply {
                addContent(
                    content = IMAGE,
                    imageUrl = shop.logo1Url,
                    imageWidth = RECEIPT_GRID_SIZE * 8
                )
            }
            )

            if (!receipt.isFirstPrinting) {
                add(PdfLine().apply {
//                    addEmptyContent()
                    addContent(
                        content = getStringResource(R.string.reprinting),
                        alignment = Paint.Align.CENTER,
                        startPos = XAxisPosition.CENTER
                    )
                }
                )
            }

            add(PdfLine().apply {
                addContent(
                    content = "領収書",
                    yOffset = RECEIPT_GRID_SIZE,
                    canMultiLines = true,
                    textSize = RECEIPT_TEXT_SIZE_LARGE,
                    alignment = Paint.Align.CENTER,
                    startPos = XAxisPosition.CENTER
                )
            })

            add(PdfLine().apply {
                addContent(
                    content = "＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿様",
                    yOffset = RECEIPT_GRID_SIZE * 2,
                    canMultiLines = true,
                    textSize = RECEIPT_TEXT_SIZE_DEFAULT
                )
            })


            add(PdfLine().apply { addContent(content = company.companyName, canMultiLines = true) })
            add(PdfLine().apply { addContent(content = shop.shopName, canMultiLines = true) })
            add(PdfLine().apply { addContent(content = shop.postalCode) })
            add(PdfLine().apply { addContent(content = shop.address, canMultiLines = true) })
            add(PdfLine().apply { addContent(content = shop.phoneNumber) })

            add(PdfLine().apply {
                addContent(
                    yOffset = RECEIPT_GRID_SIZE / 2,
                    content = receipt.createdAt!!.toReceiptDateTimeString()
                )

                addEmptyContent()
                addEmptyContent()
                addContent(content = receipt.receiptCode)

            })
            add(PdfLine().apply { addContent(content = SEPARATOR) })
        }
    }

    private fun buildSignature(): ArrayList<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(PdfLine().apply { addContent(content = SEPARATOR) })

            add(PdfLine().apply {
                addContent(
                    content = "＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿様",
                    yOffset = RECEIPT_GRID_SIZE / 2,
                    canMultiLines = true,
                    textSize = RECEIPT_TEXT_SIZE_DEFAULT
                )
            })

        }
    }

    private fun buildFooter(shop: ShopInfo): ArrayList<PdfLine> {
        return ArrayList<PdfLine>().apply {
            add(PdfLine().apply { addContent(content = SEPARATOR) })
            if (!shop.receiptStamp.isNullOrBlank()) {
                add(PdfLine().apply {
                    addContent(
                        content = shop.receiptStamp,
                        lettersPerLine = RECEIPT_TAX_PRINT_LETTER_PER_LINE_DEFAULT,
                        isFramed = true
                    )
                }
                )
                /* add(PdfLine().apply {
                     addContent(content = shop.receiptStamp)
                 }
                 )*/
                add(PdfLine().apply {
                    addContent(content = SEPARATOR)
                }
                )
            }

            add(PdfLine().apply {
                addContent(content = shop.receiptFooter, canMultiLines = true)
            })
            add(PdfLine().apply {
                addContent(
                    content = IMAGE,
                    imageUrl = shop.logo2Url,
                    imageWidth = RECEIPT_GRID_SIZE * 6
                )
            })
        }
    }
}

