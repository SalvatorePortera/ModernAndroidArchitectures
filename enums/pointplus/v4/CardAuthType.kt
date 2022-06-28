package com.nereus.craftbeer.enums.pointplus.v4

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

/**
 * Card auth type
 *
 * @property text
 * @property value
 * コンストラクタ  Card auth type
 */
enum class CardAuthType(private val text: String, private val value: Int) {
    JIS1(getStringResource(R.string.card_auth_jis1), 1),
    JIS2(getStringResource(R.string.card_auth_jis2), 2),
    MANUAL(getStringResource(R.string.card_auth_manual), 3),
    BARCODE(getStringResource(R.string.card_auth_barcode), 4),
    ONE_TIME_TOKEN(getStringResource(R.string.card_auth_one_time_token), 5),
    IC_CARD(getStringResource(R.string.card_auth_ic), 9);

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
        fun getByValue(value: Int): CardAuthType? {
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
        fun getDisplayName(value: Int): String {
            return getByValue(value)?.getName() ?: EMPTY_STRING
        }
    }
}