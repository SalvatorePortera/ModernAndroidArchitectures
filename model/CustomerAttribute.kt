package com.nereus.craftbeer.model

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.SLASH
import com.nereus.craftbeer.enums.AgeRange
import com.nereus.craftbeer.enums.Gender
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.util.getStringResource

/**
 * Customer attribute
 *
 * @property ageRange
 * @property gender
 * @property paymentMethod
 * @property isTakeAway
 * @constructor Create empty Customer attribute
 */
data class CustomerAttribute(
    var ageRange: Short? = AgeRange.AGE_20.getValue(),
    var gender: Short? = Gender.MALE.getValue(),
    var paymentMethod: Short? = null,
    var isTakeAway: Boolean? = false
) {
    /**
     * Get age range string
     *
     * @return
     */
    fun getAgeRangeString(): String {
        return ageRange?.let { AgeRange.getDisplayName(it) } ?: EMPTY_STRING
    }

    /**
     * Get payment method string
     *
     * @return
     */
    fun getPaymentMethodString(): String {
        return paymentMethod?.let { PaymentMethod.getDisplayName(it) } ?: EMPTY_STRING
    }

    /**
     * Get take away string
     *
     * @return
     */
    fun getTakeAwayString(): String {
        return if (isTakeAway == true) getStringResource(R.string.label_take_out) else getStringResource(R.string.label_take_in)
    }

    /**
     * Get gender string
     *
     * @return
     */
    fun getGenderString(): String {
        return gender?.let { Gender.getDisplayName(it) } ?: EMPTY_STRING
    }

    /**
     * Get age gender string
     *
     * @return
     */
    fun getAgeGenderString(): String {
        return StringBuilder().append(getAgeRangeString()).append(SLASH).append(getGenderString())
            .toString()
    }
}





