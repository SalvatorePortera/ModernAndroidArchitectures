package com.nereus.craftbeer.model.pointplus.v4.transactions

import com.nereus.craftbeer.enums.pointplus.v4.TransactionsType
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

/**
 * Define XML base models for Point+ Plus API v4.0.3
 *
 */
@Root(name = "transactions")
abstract class Transactions {

    @field:Attribute(name = "type")
    var type: String? = null
}

/**
 * Request transactions
 *
 * コンストラクタ  Request transactions
 */
abstract class RequestTransactions : Transactions() {

    init {
        type = TransactionsType.REQUEST.getValue()
    }

}

/**
 * Response transactions
 *
 * コンストラクタ  Response transactions
 */
abstract class ResponseTransactions() : Transactions() {

    init {
        type = TransactionsType.RESPONSE.getValue()
    }
}

/**
 * Transaction
 *
 * コンストラクタ  Transaction
 */
abstract class Transaction


