package com.nereus.craftbeer.enums.pointplus.v4

import timber.log.Timber

enum class DefaultService(private val value: Int) {
    APPLY( 1),
    DONT_APPLY( 3);

    fun getValue(): Int {
        return this.value
    }

    companion object {
        fun getByValue(value: Int): DefaultService? {
            return try {
                values().first { it.value == value }
            } catch (ex: NoSuchElementException) {
                Timber.e(ex)
                null
            }
        }
    }
}