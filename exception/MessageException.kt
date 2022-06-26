package com.nereus.craftbeer.exception

import com.nereus.craftbeer.model.MessagesModel
import java.util.concurrent.Callable

class MessageException(
    private val messagesModel: MessagesModel,
    cause: Throwable? = null,
    private var callback: Callable<Unit>? = null
) : RuntimeException(cause) {

    fun setCallback(callback: Callable<Unit>) {
        this.callback = callback
    }

    fun getCallback(): Callable<Unit>? {
        return callback
    }

    fun getMessageModel() : MessagesModel {
        return messagesModel
    }
}
