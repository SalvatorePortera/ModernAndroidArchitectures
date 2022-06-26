package com.nereus.craftbeer.model

import androidx.annotation.StringRes
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.util.getStringResource

/**
 * Messages model
 *
 * @constructor Create empty Messages model
 */
class MessagesModel {
    private var tabletMessages = mutableListOf<String>()

    private var coreMessage: String? = null

    private var eventType: String = ""

    private var errorLogCode: ErrorLogCode? = null

    private var messageLogCode : MessageLogCode? = null

    private var obnizId: String? = null

    constructor()

    constructor(@StringRes resId: Int, vararg formatArgs: Any) {
        addTabletMessage(resId, *formatArgs)
    }


    constructor(messages: List<String>) {
        this.tabletMessages.addAll(messages)
    }

    constructor(message: String) {
        this.tabletMessages.add(message)
    }

    /**
     * @param coreMsgArgs: params for core-message
     * @param tabletMsgArgs: params for tablet-message
     */
    constructor(errorLogCode: ErrorLogCode, obnizId: String? = null, coreMsgArgs: List<Any> = emptyList(), tabletMsgArgs: List<Any> = emptyList()) {
        this.errorLogCode = errorLogCode
        this.obnizId = obnizId

        /*If message is display on tablet*/
        if (errorLogCode.hasTabletMessage()) {
            addTabletMessage(errorLogCode.getTabletMessage(*tabletMsgArgs.toTypedArray()))
        }

        this.coreMessage = errorLogCode.getCoreMessage(*coreMsgArgs.toTypedArray())
    }

    /**
     * @param coreMsgArgs: params for core-message
     * @param tabletMsgArgs: params for tablet-message
     */
    constructor(messageLogCode: MessageLogCode, eventType: String, obnizId: String? = null, coreMsgArgs: List<Any> = emptyList(), tabletMsgArgs: List<Any> = emptyList()) {
        this.messageLogCode = messageLogCode
        this.obnizId = obnizId
        this.eventType = eventType

        /*If message is display on tablet*/
        if (messageLogCode.hasTabletMessage()) {
            addTabletMessage(messageLogCode.getTabletMessage(*tabletMsgArgs.toTypedArray()))
        }

        this.coreMessage = messageLogCode.getCoreMessage(*coreMsgArgs.toTypedArray())
    }

    fun addTabletMessage(message: String) {
        this.tabletMessages.add(message)
    }

    fun addTabletMessage(@StringRes resId: Int, vararg formatArgs: Any) {
        val newMessages = tabletMessages.toMutableList()
        newMessages.add(getStringResource(resId, *formatArgs))
        tabletMessages = newMessages
    }

    fun hasMessage(): Boolean {
        return tabletMessages.isNotEmpty()
    }

    fun isFromErrorLog(): Boolean {
        return getErrorLogCode() != null
    }

    fun isFromMessageLog(): Boolean {
        return getMessageLogCode() != null
    }

    fun getMessages(): List<String> {
        return tabletMessages
    }

    fun getCoreMessage(): String {
        return coreMessage ?: EMPTY_STRING
    }

    fun getErrorLogCode(): ErrorLogCode? {
        return errorLogCode
    }

    fun getMessageLogCode(): MessageLogCode? {
        return messageLogCode
    }

    fun getEventType(): String {
        return eventType
    }

    fun getObnizId(): String? {
        return obnizId
    }
}




