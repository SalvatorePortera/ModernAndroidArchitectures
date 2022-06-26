package com.nereus.craftbeer.model

class Food(var name: String, var price: Int, var id: Int, var status: Boolean) {

    override fun toString(): String {
        return "$name -Price: $price"
    }
}
