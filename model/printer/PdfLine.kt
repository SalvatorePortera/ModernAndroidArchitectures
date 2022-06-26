package com.nereus.craftbeer.model.printer

import android.graphics.Paint
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.RECEIPT_TEXT_SIZE_DEFAULT
import com.nereus.craftbeer.constant.RECEIPT_WIDTH_DEFAULT
import com.nereus.craftbeer.enums.printer.XAxisPosition
import timber.log.Timber

/**
 * Pdf line
 *
 * @property contents
 * @constructor Create empty Pdf line
 */
data class PdfLine(
    var contents: MutableList<PdfPrintModel> = ArrayList(XAxisPosition.values().size)
) {
    fun addEmptyContent() {
        addContent(content = EMPTY_STRING)
    }

    fun addContent(
        content: String = EMPTY_STRING,

        imageUrl: String = EMPTY_STRING,

        lettersPerLine: Int? = null,

        startPos: XAxisPosition? = null, // Horizontal start position for NEXT item

        endPos: XAxisPosition? = null, // Horizontal start position for NEXT item

        yOffset: Int = 0, // add height space before drawing THIS item

        isFramed: Boolean = false,

        canMultiLines: Boolean = false,

        imageWidth: Int = 0,

        alignment: Paint.Align? = null,

        textSize: Float = RECEIPT_TEXT_SIZE_DEFAULT
    ) {
        if (!contents.add(
                PdfPrintModel(
                    content = content,
                    alignment = alignment,
                    marginTop = yOffset,
                    imageWidth = imageWidth,
                    isFramed = isFramed,
                    lettersPerLine = if (canMultiLines) (21 * RECEIPT_TEXT_SIZE_DEFAULT / textSize).toInt() else lettersPerLine,
                    imageUrl = imageUrl,
                    startPos = startPos,
                    endPos = endPos,
                    textSize = textSize,
                    canMultiLines = canMultiLines
                )
            )
        ) {
            Timber.e("Pdf contents is full")
        }
    }
}






