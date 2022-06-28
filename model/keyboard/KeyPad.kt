package com.nereus.craftbeer.model.keyboard

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import kotlinx.android.synthetic.main.fragment_topup.view.*

/**
 * Key pad
 *
 * @property value
 * @property type
 * @property background
 * コンストラクタ  Key pad
 */
class KeyPad constructor(

    var value: KeyPadValue,

    var type: KeyPadType = KeyPadType.NUMBER,

    var background:  Int
) {
    enum class KeyPadType { NUMBER, FUNCTION}
    enum class KeyPadValue(val value: String) {
        DELETE_ALL("DELETE_ALL"),
        DELETE("DELETE"),
        DOUBLE_ZERO("00"),
        ZERO("0"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9")
    }

    /**
     * Get background
     *
     * @return
     */
    fun getBackground() : Int? {
        return when (value) {
            KeyPadValue.ONE -> R.drawable.beer
            else -> null
        }
    }
}




