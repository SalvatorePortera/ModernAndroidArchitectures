package com.nereus.craftbeer.enums.pointplus.v4

/**
 * Transactions type
 *
 * @property value
 * コンストラクタ  Transactions type
 */
enum class TransactionsType(private val value: String) {
    REQUEST("request"),
    RESPONSE("response");

    fun getValue(): String {
        return this.value
    }
}