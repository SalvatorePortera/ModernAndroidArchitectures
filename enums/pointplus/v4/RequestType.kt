package com.nereus.craftbeer.enums.pointplus.v4

import com.nereus.craftbeer.constant.REQUIRED_FIELDS_CARD

enum class RequestType(private val value: String, private val requiredFields: List<String>) {
    QUERY_BALANCE("query_balance", REQUIRED_FIELDS_CARD),
    VALUE_CHARGE("value_charge", REQUIRED_FIELDS_CARD),
    VALUE_CHARGE_CANCELLATION("value_charge_cancel", REQUIRED_FIELDS_CARD),
    VALUE_PAYMENT("value_payment", REQUIRED_FIELDS_CARD),
    VALUE_PAYMENT_CANCELLATION("value_payment_cancel", REQUIRED_FIELDS_CARD);

    /**
     * Get value
     *
     * @return
     */
    fun getValue(): String {
        return this.value
    }

    /**
     * Is require
     *
     * @param field
     * @return
     */
    fun isRequire(field: String): Boolean {
        return this.requiredFields.contains(field)
    }
}