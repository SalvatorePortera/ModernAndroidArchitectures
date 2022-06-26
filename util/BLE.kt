package com.nereus.craftbeer.util

import java.util.*
import javax.smartcardio.ATR

/**
 * Ble
 *
 * @constructor Create empty Ble
 */
class BLE {
    companion object {
        private const val ATR_FeliCa = "11 00 3B"

        fun isFeliCa(atr: ATR): Boolean {
            return atr.bytes.size > 16 && ATR_FeliCa == Hex.toHexString(
                Arrays.copyOfRange(
                    atr.bytes,
                    12,
                    15
                )
            )
        }
    }

}