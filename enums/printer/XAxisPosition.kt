package com.nereus.craftbeer.enums.printer

import com.nereus.craftbeer.constant.RECEIPT_GRID_SIZE
import com.nereus.craftbeer.constant.RECEIPT_WIDTH_DEFAULT

// Support 4 columns in receipt
enum class XAxisPosition(private val value: Int) {
    COLUMN_1(2),
    BETWEEN_1_AND_2((RECEIPT_WIDTH_DEFAULT + RECEIPT_GRID_SIZE) / 4 + 1),
    COLUMN_2(RECEIPT_WIDTH_DEFAULT / 2 + RECEIPT_GRID_SIZE / 2),
    CENTER(RECEIPT_WIDTH_DEFAULT / 2),
    COLUMN_3(RECEIPT_WIDTH_DEFAULT / 2 + RECEIPT_GRID_SIZE * 2),
    BETWEEN_3_AND_4(RECEIPT_WIDTH_DEFAULT * 3 / 4 + RECEIPT_GRID_SIZE - 1),
    COLUMN_4(RECEIPT_WIDTH_DEFAULT - 2);

    fun getValue(): Int {
        return this.value
    }

    fun next(): XAxisPosition {
        return when (this) {
            COLUMN_1 -> COLUMN_2
            COLUMN_2 -> COLUMN_3
            COLUMN_3 -> COLUMN_4
            COLUMN_4 -> COLUMN_1
            else -> COLUMN_1
        }
    }
}