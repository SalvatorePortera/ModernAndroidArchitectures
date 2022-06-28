package com.nereus.craftbeer.enums.pointplus.v4

import timber.log.Timber

/**
 * Default service
 *
 * @property value
 * コンストラクタ  Default service
 */
enum class DefaultService(private val value: Int) {
    APPLY( 1),
    DONT_APPLY( 3);

    /**
     * Get value
     *
     * @return
     */
    fun getValue(): Int {
        return this.value
    }

    companion object {

        /**
         * Get by value
         *
         * @param value
         * @return
         */
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