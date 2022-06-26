package com.nereus.craftbeer.socket

import com.nereus.craftbeer.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import java.net.URISyntaxException


object SocketIO {
    private lateinit var mSocket: Socket
    private var aliveSocket: Socket? = null


    fun getSocket(): Socket? {
        Timber.d("Obniz URL %s", BuildConfig.OBNIZ_SOCKET_URL)
        mSocket = try {
            IO.socket(BuildConfig.OBNIZ_SOCKET_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        return mSocket
    }

    fun getAliveSocket(tokenValue: String): Socket? {

        if (aliveSocket != null && aliveSocket!!.connected()) {
            aliveSocket!!.disconnect()
        }

        aliveSocket = try {
            val mOptions = IO.Options()
            mOptions.transports = Array(1) { "websocket" }
            mOptions.forceNew = true
            mOptions.query = "token=$tokenValue"
            IO.socket(BuildConfig.CORE_SYSTEM_SOCKET, mOptions)
        } catch (e: Exception) {
            throw java.lang.RuntimeException(e)
        }
        return aliveSocket
    }
}
