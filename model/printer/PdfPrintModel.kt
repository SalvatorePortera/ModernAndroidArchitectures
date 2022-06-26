package com.nereus.craftbeer.model.printer

import android.graphics.Color
import android.graphics.Paint
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.printer.XAxisPosition

/**
 * Pdf print model
 *
 * @property content
 * @property imageUrl
 * @property lettersPerLine
 * @property startPos
 * @property endPos
 * @property marginTop
 * @property isFramed
 * @property canMultiLines
 * @property imageWidth
 * @property alignment
 * @property textSize
 * @constructor Create empty Pdf print model
 */
data class PdfPrintModel(

    var content: String = EMPTY_STRING,

    var imageUrl: String = EMPTY_STRING,

    var lettersPerLine: Int? = null,

    var startPos: XAxisPosition? = null, // Horizontal start position for THIS item

    var endPos: XAxisPosition? = null, // Horizontal end position for THIS item

    var marginTop: Int = 0, // add height space before drawing THIS item

    var isFramed: Boolean = false,

    var canMultiLines: Boolean = false,

    var imageWidth: Int = 0,

    var alignment: Paint.Align? = null,

    var textSize: Float = RECEIPT_TEXT_SIZE_DEFAULT
) {
    companion object {
        val PAINT_MAP = mapOf<String, Paint>(
            Pair(TEXT, getTextPaint()),
            Pair(LOGO, getLogoPaint()),
            Pair(QR, getLogoPaint()),
            Pair(SEPARATOR, getLogoPaint())
        )

        /**
         * Get text paint
         *
         * @return
         */
        fun getTextPaint(): Paint {
            val textPaint = Paint()
            textPaint.color = Color.BLACK
            textPaint.textSize = RECEIPT_TEXT_SIZE_DEFAULT
            return textPaint
        }

        /**
         * Get logo paint
         *
         * @return
         */
        private fun getLogoPaint(): Paint {
            val textPaint = Paint()
            textPaint.color = Color.BLACK
            textPaint.textSize = RECEIPT_TEXT_SIZE_DEFAULT
            return textPaint
        }

        /**
         * Get qr paint
         *
         * @return
         */
        private fun getQrPaint(): Paint {
            val textPaint = Paint()
            textPaint.color = Color.BLACK
            textPaint.textSize = RECEIPT_TEXT_SIZE_DEFAULT
            return textPaint
        }
    }
}







