package com.nereus.craftbeer.enums

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

enum class AgeRange(private val text: String, private val value: Short) {
    AGE_10(getStringResource(R.string.age_10), 1),
    AGE_20(getStringResource(R.string.age_20), 2),
    AGE_30(getStringResource(R.string.age_30), 3),
    AGE_40(getStringResource(R.string.age_40), 4),
    AGE_50(getStringResource(R.string.age_50), 5);

    fun getName(): String {
        return this.text
    }

    fun getValue(): Short {
        return this.value
    }

    companion object {
        private fun getByValue(value: Short): AgeRange? {
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