package com.nereus.craftbeer.util

import android.content.Context
import com.google.firebase.crashlytics.internal.Logger
import timber.log.Timber
import javax.smartcardio.*


private lateinit var context: Context

class NfcUtil(contexts: Context) {

    init {
        context = contexts
    }

    fun exchangeAPDU(commandString: String, channel: CardChannel): ResponseAPDU {
        val commandAPDU = CommandAPDU(Hex.stringToBytes(commandString))
        Timber.tag(Logger.TAG).i("CommandAPDU: %s", Hex.bytesToHexString(commandAPDU.bytes))
        return channel.transmit(commandAPDU);
    }

    fun connect(mTerminal: CardTerminal): Card {
        val card = mTerminal.connect("*")

        if (!BLE.isFeliCa(card!!.atr)) {
            card.disconnect(true);
            throw CardException("Invalid card type.")
        }

        Timber.tag(Logger.TAG).i("Card ATR: %s", Hex.toHexString(card.atr.bytes))
        Timber.tag(Logger.TAG).i(card.toString())
        return card
    }

}

