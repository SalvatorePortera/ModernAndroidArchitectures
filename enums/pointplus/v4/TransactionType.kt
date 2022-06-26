package com.nereus.craftbeer.enums.pointplus.v4

import timber.log.Timber

enum class TransactionType(private val value: String) {
    UN_RESEND( "0"),
    RESEND( "1");

    fun getValue(): String {
        return this.value
    }

    companion object {
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