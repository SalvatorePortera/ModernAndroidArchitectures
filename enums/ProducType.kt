package com.nereus.craftbeer.enums

import androidx.annotation.StringRes
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

enum class ProducType(private val text: String, private val value: Short, @StringRes private val formatedAmount: Int) {
    GOODS(getStringResource(R.string.goods), 1, R.string.formatedQuantity),
    TAP_BEER(getStringResource(R.string.tap_beer), 2, R.string.formatedMililiter),
    TOP_UP(getStringResource(R.string.tap_beer), 3, R.string.formatedTopUpAmount);

    fun getName(): String {
        return this.text
    }

    fun getValue(): Short {
        return this.value
    }

    fun formatedAmount(): Int {
        return this.formatedAmount
    }

    companion object {
        fun getByValue(value: Short): ProducType? {
            return try {
                values().first { it.value == value }
            } catch (ex: NoSuchElementException) {
                Timber.e(ex)
                null
            }
        }

        fun getDisplayName(value: Short): String {
            return getByValue(value)?.getName() ?: EMPTY_STRING
        }
    }
}