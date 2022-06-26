package com.nereus.craftbeer.enums.pointplus.v4

enum class TransactionsType(private val value: String) {
    REQUEST("request"),
    RESPONSE("response");

    fun getValue(): String {
        return this.value
    }
}