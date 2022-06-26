package com.nereus.craftbeer.exception

import com.nereus.craftbeer.model.MessagesModel
import java.util.concurrent.Callable

/**
 * Message exception
 *
 * @property messagesModel
 * @property callback
 * @constructor
 *
 * @param cause
 */
class MessageException(
    private val messagesModel: MessagesModel,
    cause: Throwable? = null,
    private var callback: Callable<Unit>? = null
) : RuntimeException(cause) {

    /**
     * Set callback
     *
     * @param callback
     */
    fun setCallback(callback: Callable<Unit>) {
        this.callback = callback
    }

    /**
     * Get callback
     *
     * @return
     */
    fun getCallback(): Callable<Unit>? {
        return callback
    }

    /**
     * Get message model
     *
     * @return
     */
    fun getMessageModel() : MessagesModel {
        return messagesModel
    }
}
