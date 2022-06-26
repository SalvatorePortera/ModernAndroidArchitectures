package com.nereus.craftbeer.enums

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.getStringResource
import timber.log.Timber

enum class PaymentMethod(private val text: String, private val value: Short) {
    PAYMENT_HOUSE_MONEY(getStringResource(R.string.payment_house_money), 1),
    PAYMENT_CASH(getStringResource(R.string.payment_cash), 2),
    PAYMENT_ELECTRONIC_MONEY(getStringResource(R.string.payment_emoney), 3),
    PAYMENT_CREDIR_CARD(getStringResource(R.string.payment_credit_card), 4),
    PAYMENT_QR(getStringResource(R.string.payment_qr), 5),
    PAYMENT_OTHERS(getStringResource(R.string.payment_others), 6);

    fun getName(): String {
        return this.text
    }

    fun getValue(): Short {
        return this.value
    }

    companion object {
        fun getByValue(value: Short): PaymentMethod? {
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