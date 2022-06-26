package com.nereus.craftbeer.util.keyboard

import androidx.annotation.IdRes
import com.nereus.craftbeer.R
import com.nereus.craftbeer.model.keyboard.KeyPad

/**
 * Append
 *
 * @param value
 * @return
 */
private fun String.append(value: String): String {
    return StringBuilder(this).append(value).toString()
}

/**
 * Remove last
 *
 * @return
 */
private fun String.removeLast(): String {
    return if (length <= 1) KeyPad.KeyPadValue.ZERO.value else substring(0, lastIndex)
}

/**
 * Input
 *
 * @param currentValue
 * @param keypadCode
 * @return
 */
fun input(currentValue: String, keypadCode: KeyPad.KeyPadValue): Int {
    return when (keypadCode) {
        KeyPad.KeyPadValue.DELETE -> currentValue.removeLast()
        KeyPad.KeyPadValue.DELETE_ALL -> KeyPad.KeyPadValue.ZERO.value
        else -> currentValue.append(keypadCode.value)
    }.toIntOrNull() ?: 0
}

/**
 * Get key pad value
 *
 * @param buttonId
 * @return
 */
fun getKeyPadValue(@IdRes buttonId: Int): KeyPad.KeyPadValue {
    return when (buttonId) {
        R.id.btn_one -> KeyPad.KeyPadValue.ONE
        R.id.btn_two -> KeyPad.KeyPadValue.TWO
        R.id.btn_three -> KeyPad.KeyPadValue.THREE
        R.id.btn_four -> KeyPad.KeyPadValue.FOUR
        R.id.btn_five -> KeyPad.KeyPadValue.FIVE
        R.id.btn_six -> KeyPad.KeyPadValue.SIX
        R.id.btn_seven -> KeyPad.KeyPadValue.SEVEN
        R.id.btn_eight -> KeyPad.KeyPadValue.EIGHT
        R.id.btn_nine -> KeyPad.KeyPadValue.NINE
        R.id.btn_zero -> KeyPad.KeyPadValue.ZERO
        R.id.btn_double_zero -> KeyPad.KeyPadValue.DOUBLE_ZERO
        else -> KeyPad.KeyPadValue.DELETE
    }
}
