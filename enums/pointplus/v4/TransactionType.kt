package com.nereus.craftbeer.enums.pointplus.v4

import timber.log.Timber

/**
 * Transaction type
 *
 * @property value
 * @constructor Create empty Transaction type
 */
enum class TransactionType(private val value: String) {
    UN_RESEND( "0"),
    RESEND( "1");

    /**
     * Get value
     *
     * @return
     */
    fun getValue(): String {
        return this.value
    }

    companion object {

        /**
         * Get by value
         *
         * @param value
         * @return
         */
        fun getByValue(value: String): TransactionType? {
            return try {
                values().first { it.value == value }
            } catch (ex: NoSuchElementException) {
                Timber.e(ex)
                null
            }
        }
    }
}