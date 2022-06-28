package com.nereus.craftbeer.model

/**
 * Food
 *
 * @property name
 * @property price
 * @property id
 * @property status
 * コンストラクタ  Food
 */
class Food(var name: String, var price: Int, var id: Int, var status: Boolean) {

    /**
     * To string
     *
     * @return
     */
    override fun toString(): String {
        return "$name -Price: $price"
    }
}
