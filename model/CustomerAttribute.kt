package com.nereus.craftbeer.model

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.SLASH
import com.nereus.craftbeer.enums.AgeRange
import com.nereus.craftbeer.enums.Gender
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.util.getStringResource

data class CustomerAttribute(
    var ageRange: Short? = AgeRange.AGE_20.getValue(),
    var gender: Short? = Gender.MALE.getValue(),
    var paymentMethod: Short? = null,
    var isTakeAway: Boolean? = false
) {
    fun getAgeRangeString(): String {
        return ageRange?.let { AgeRange.getDisplayName(it) } ?: EMPTY_STRING
    }

    fun getPaymentMethodString(): String {
        return paymentMethod?.let { PaymentMethod.getDisplayName(it) } ?: EMPTY_STRING
    }

    fun getTakeAwayString(): String {
        return if (isTakeAway == true) getStringResource(R.string.label_take_out) else getStringResource(R.string.label_take_in)
    }

    fun getGenderString(): String {
        return gender?.let { Gender.getDisplayName(it) } ?: EMPTY_STRING
    }

    fun getAgeGenderString(): String {
        return StringBuilder().append(getAgeRangeString()).append(SLASH).append(getGenderString())
            .toString()
    }
}





