package com.nereus.craftbeer.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.annotation.RawRes
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.printer.XAxisPosition
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.printer.PdfLine
import com.nereus.craftbeer.model.printer.PdfPrintModel
import com.nereus.craftbeer.realm.RealmApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*

fun getShopInfoPref(): SharedPreferences {
    return RealmApplication.instance.getSharedPreferences(
        SHARED_PREF_RECEIPT_FILE,
        Context.MODE_PRIVATE
    )
}

fun getDeviceInfoPref(): SharedPreferences {
    return RealmApplication.instance.getSharedPreferences(
        PREF_DEVICE_FILE,
        Context.MODE_PRIVATE
    )
}

fun encodeFileToBase64Binary(
    outputStream: ByteArrayOutputStream,
    @RawRes pdfRawRes: Int = 0
): String? {
    try {
        outputStream.use {
            return Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)
        }
    } catch (e: FileNotFoundException) {
        throw MessageException(MessagesModel(R.string.msg_receipt_file_not_found))
    } catch (e: IOException) {
        throw MessageException(MessagesModel(R.string.msg_print_failed))
    }
}

fun Context.sendDataToPrintAgent(
    outputStream: ByteArrayOutputStream,
    receiptId: String = EMPTY_STRING,
    isMultiplePrinting: Boolean = false,
    isIssued: Boolean = false,
    isTopUp: Boolean = false
) {
    val callback = buildPrinterURI(
        isMultiplePrinting = isMultiplePrinting,
        isIssued = isIssued, receiptId = receiptId, isTopupReceipt = isTopUp
    )

    val base64 = encodeFileToBase64Binary(outputStream)
    val encodedUrl = Uri.encode(base64)
    val encodedCallback = Uri.encode(callback)
    val printerAgent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("siiprintagent://1.0/print?CallbackSuccess=${encodedCallback}&CallbackFail=${encodedCallback}&ErrorDialog=yes&Format=pdf&Data=${encodedUrl}&SelectOnError=no&CutType=partial&CutFeed=yes&FitToWidth=yes")
        )
    this.startActivity(printerAgent)
}


private suspend fun PdfPrintModel.draw(
    canvas: Canvas,
    top: Int,
    start: XAxisPosition
): Int {
    return when (content) {
        IMAGE -> {
            canvas.drawBitmapInRect(
                photoUrl = imageUrl,
                top = top + marginTop,
                imageWidth = imageWidth
            )
        }
        SEPARATOR -> {
            canvas.drawSeparator(top = top, start = start, end = endPos)
        }
        else -> {
            if (isFramed && lettersPerLine != null) { // Draw multiple lines text inside a rectangle frame
                canvas.drawTextWithFrame(
                    content = content,
                    letterPerLine = lettersPerLine!!,
                    start = start,
                    top = top + marginTop,
                    textSize = textSize
                )
            } else if (canMultiLines) {
                // Draw normal text
                canvas.drawMultiLinesText(
                    content = content,
                    letterPerLine = lettersPerLine!!,
                    start = start,
                    top = top + marginTop,
                    textSize = textSize,
                    textAlign = alignment,
                )
            } else {
                // Draw normal text
                canvas.drawNormalText(
                    content = content,
                    top = top + marginTop,
                    start = start,
                    textAlign = alignment,
                    textSize = textSize
                )
            }
        }
    }
}

suspend fun buildPdf(lines: List<PdfLine>, canvasHeight: Int): PdfDocument {
    val doc = PdfDocument()
    // start a page
    var page = doc.startNewPage(canvasHeight)
    var canvas = page.canvas

    var topPos = RECEIPT_LINE_SPACE

    for (line in lines) {
        var startPos = XAxisPosition.COLUMN_1
        var nextTopPos = topPos
        for (item in line.contents) {
            startPos = item.startPos ?: startPos
            val tempTopPos = item.draw(top = topPos, start = startPos, canvas = canvas)
            startPos = startPos.next()
            if (tempTopPos > nextTopPos) {
                nextTopPos = tempTopPos
            }
        }
        topPos = nextTopPos
    }

    doc.finishPage(page)
    return doc
}

suspend fun getHeightFromBuildPdf(lines: List<PdfLine>, canvasHeight: Int): Int {
    val doc = PdfDocument()
    // start a page
    var page = doc.startNewPage(canvasHeight)
    var canvas = page.canvas

    var topPos = RECEIPT_LINE_SPACE

    for (line in lines) {
        var startPos = XAxisPosition.COLUMN_1
        var nextTopPos = topPos
        for (item in line.contents) {
            startPos = item.startPos ?: startPos
            val tempTopPos = item.draw(top = topPos, start = startPos, canvas = canvas)
            startPos = startPos.next()
            if (tempTopPos > nextTopPos) {
                nextTopPos = tempTopPos
            }
        }
        topPos = nextTopPos
    }
    Timber.d("generate receipt toppos: %d", topPos)
    doc.finishPage(page)
    return topPos
}


private fun PdfDocument.startNewPage(canvasHeight: Int): PdfDocument.Page {
    val pageInfo =
        PageInfo.Builder(RECEIPT_WIDTH_DEFAULT, canvasHeight, pages.size + 1).create()
    return startPage(pageInfo)
}

suspend fun Canvas.drawBitmapInRect(
    photoUrl: String,
    top: Int,
    imageWidth: Int,
    nextOffset: Int = RECEIPT_LINE_SPACE
): Int {
    try {

        val paint = Paint()
        paint.setColor(Color.WHITE)

        val bitmap = getBitmap(photoUrl, imageWidth * 3)
        val imageHeight = imageWidth * bitmap.height / bitmap.width
        val left = (width - imageWidth) / 2
        val rect =
            Rect(left, top, width - left, top + imageHeight)

        drawBitmap(
            bitmap,
            null,
            rect,
            paint
        )
        return rect.bottom + nextOffset
    } catch (ex: Exception) {
        Timber.d(ex, "Failed to get shop image")
        return top
    }
}

fun Canvas.drawMultiLinesText(
    content: String,
    letterPerLine: Int,
    top: Int,
    start: XAxisPosition = XAxisPosition.COLUMN_1,
    nextOffset: Int = RECEIPT_LINE_SPACE,
    textAlign: Paint.Align? = null,
    textSize: Float = RECEIPT_LINE_SPACE.toFloat()
): Int {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.textAlign = textAlign ?: getAlignment(start)
    paint.textSize = textSize

    var startPos = 0
    val lineSpace = textSize
    var nextTop = top.toFloat()

    var letterPerLine = if (letterPerLine < content.length) {
        letterPerLine
    } else content.length

    val width = paint.measureText(content.substring(0, letterPerLine)) + lineSpace * 2
    var height = lineSpace * 2
    while (startPos < content.length) {
        val endPos = if (startPos + letterPerLine >= content.length) {
            content.length
        } else startPos + letterPerLine
        drawText(
            content,
            startPos,
            endPos,
            start.getValue().toFloat(),
            nextTop,
            paint
        )
        startPos += letterPerLine
        nextTop += lineSpace
        height += lineSpace
    }

    return nextTop.toInt()
}

fun Canvas.drawTextWithFrame(
    content: String,
    letterPerLine: Int,
    top: Int,
    start: XAxisPosition = XAxisPosition.COLUMN_1,
    nextOffset: Int = RECEIPT_LINE_SPACE,
    textSize: Float = RECEIPT_LINE_SPACE.toFloat()
): Int {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.textSize = textSize

    var startPos = 0
    val lineSpace = textSize
    var nextTop = top + lineSpace * 2

    var letterPerLine = if (letterPerLine < content.length) {
        letterPerLine
    } else content.length

    val width = paint.measureText(content.substring(0, letterPerLine)) + lineSpace * 2
    var height = lineSpace * 2
    while (startPos < content.length) {
        val endPos = if (startPos + letterPerLine >= content.length) {
            content.length
        } else startPos + letterPerLine
        drawText(
            content,
            startPos,
            endPos,
            start.getValue().toFloat() + textSize,
            nextTop,
            paint
        )
        startPos += letterPerLine
        nextTop += lineSpace
        height += lineSpace
    }

    return drawRectangle(width = width.toInt(), height = height.toInt(), top = top)
}

fun Canvas.drawNormalText(
    content: String,
    top: Int,
    start: XAxisPosition = XAxisPosition.COLUMN_1,
    nextOffset: Int = RECEIPT_LINE_SPACE,
    textAlign: Paint.Align? = null,
    textSize: Float = RECEIPT_TEXT_SIZE_DEFAULT
): Int {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.textAlign = textAlign ?: getAlignment(start)
    paint.textSize = textSize

    //    val plain: Typeface =
    //        Typeface.createFromAsset(RealmApplication.instance.assets, "fonts/03SmartFontUI.ttf")
    paint.typeface = Typeface.MONOSPACE
    drawText(
        content,
        start.getValue().toFloat(),
        top.toFloat(),
        paint
    )

    return top + nextOffset
}

private fun getAlignment(start: XAxisPosition) =
    if (start == XAxisPosition.COLUMN_2 || start == XAxisPosition.COLUMN_4) {
        Paint.Align.RIGHT
    } else Paint.Align.LEFT

fun Canvas.drawRectangle(
    width: Int,
    height: Int,
    top: Int,
    start: Int = XAxisPosition.COLUMN_1.getValue(),
    nextYOffset: Int = RECEIPT_LINE_SPACE * 2
): Int {
    val paint = Paint()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1F
    drawRect(Rect(start, top, start + width, top + height), paint)
    return top + height + nextYOffset
}

fun Canvas.drawSeparator(
    top: Int,
    startOffset: Int = 0,
    start: XAxisPosition = XAxisPosition.COLUMN_1,
    end: XAxisPosition? = null,
    nextYOffset: Int = RECEIPT_LINE_SPACE * 2
): Int {
    val paint = Paint()
    paint.setStyle(Paint.Style.STROKE)
    paint.strokeWidth = 1F
    val startX = start.getValue() + startOffset
    val endX = end?.getValue() ?: width - startX
    drawLine(startX.toFloat(), top.toFloat(), endX.toFloat(), top.toFloat(), paint)
    return top + nextYOffset
}

fun PdfDocument.writeFileOnInternalStorage(sFileName: String?): String {
    val dir =
        RealmApplication.instance.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    if (dir == null) {
        throw MessageException(MessagesModel(R.string.msg_print_failed))
    }
    if (!dir.exists()) {
        dir.mkdir()
    }
    try {
        val gpxfile = File(dir, sFileName)
        FileOutputStream(gpxfile).use {
            this.writeTo(it)
        }
        return gpxfile.absolutePath
    } catch (e: Exception) {
        throw MessageException(MessagesModel(R.string.msg_internal_exception))
    } finally {
        this.close()
    }
    return ""
}

suspend fun List<PdfDocument>.writeToInternalStorage() {
    forEachIndexed { index, document ->
        withContext(Dispatchers.IO) {
            document.writeFileOnInternalStorage("receipt_${index}.pdf")
            document.close()
        }
    }
}

suspend fun Context.printReceipt(
    document: PdfDocument,
    receiptId: String = EMPTY_STRING,
    isMultiplePrinting: Boolean = false,
    isIssued: Boolean = false,
    isTopUp: Boolean = false
) {
    withContext(Dispatchers.Default) {
        ByteArrayOutputStream().use {
            document.writeTo(it)
            sendDataToPrintAgent(
                it,
                isMultiplePrinting = isMultiplePrinting,
                isIssued = isIssued,
                receiptId = receiptId,
                isTopUp = isTopUp
            )
        }
    }

    withContext(Dispatchers.IO) {
        document.writeFileOnInternalStorage("receipt_${receiptId}.pdf")
        document.close()
    }
}

suspend fun buildPdfDocument(
    receipt: List<PdfLine>,
    canvasHeight: Int
): PdfDocument {
    return withContext(Dispatchers.Default) {
        buildPdf(receipt, canvasHeight)
    }
}

suspend fun getHeightFromBuildPdfDocument(
    receipt: List<PdfLine>,
    canvasHeight: Int
): Int {
    return withContext(Dispatchers.Default) {
        getHeightFromBuildPdf(receipt, canvasHeight)
    }
}

fun buildPrinterURI(
    isMultiplePrinting: Boolean = false,
    isIssued: Boolean = false,
    isTopupReceipt: Boolean = false,
    receiptId: String
): String {
    var uri = StringBuilder(PRINTER_VIEW_BASE_URI)
    uri.append(if (isMultiplePrinting) PRINTER_VIEW_PATH_PRINT_MULTIPLE else PRINTER_VIEW_PATH_PRINT_ONE)
    uri.append(if (isTopupReceipt) PRINTER_VIEW_PATH_TOP_UP else PRINTER_VIEW_PATH_SALE_LOG)
    uri.append(if (isIssued) PRINTER_VIEW_PATH_ISSUE else EMPTY_STRING)
    uri.append(SLASH)
    uri.append(receiptId)
    return uri.toString()
}

fun extractReceiptIdFromPrinterPath(path: String): String {
    var receiptId = EMPTY_STRING
    path.replace("\\d.*".toRegex()) { receiptId = it.value; EMPTY_STRING }
    return receiptId
}
