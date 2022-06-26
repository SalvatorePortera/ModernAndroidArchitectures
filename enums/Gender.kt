package com.nereus.craftbeer.enums

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

/**
 * Gender
 *
 * @property text
 * @property value
 * @constructor Create empty Gender
 */
enum class Gender(private val text: String, private val value: Short) {
    MALE(getStringResource(R.string.gender_male), 1),
    FEMALE(getStringResource(R.string.gender_female), 2),
    OTHER(getStringResource(R.string.gender_other), 3);

    /**
     * Get name
     *
     * @return
     */
    fun getName(): String {
        return this.text
    }

    /**
     * Get value
     *
     * @return
     */
    fun getValue(): Short {
        return this.value
    }

    companion object {

        /**
         * Get by value
         *
         * @param value
         * @return
         */
        fun getByValue(value: Short): Gender? {
            return try {
                values().first { it.value == value }
            } catch (ex: NoSuchElementException) {
                Timber.e(ex)
                null
            }
        }

        /**
         * Get display name
         *
         * @param value
         * @return
         */
        fun getDisplayName(value: Short): String {
            return getByValue(value)?.getName() ?: EMPTY_STRING
        }
    }
}