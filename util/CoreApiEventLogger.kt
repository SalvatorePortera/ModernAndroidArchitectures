package com.nereus.craftbeer.util

import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.repository.ErrorLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.lang.Exception

/**
 * Core api event logger
 *
 * @property errorLogRepository
 * コンストラクタ  Core api event logger
 */
class CoreApiEventLogger(private val errorLogRepository: ErrorLogRepository) : HttpLoggingInterceptor.Logger {
    private val KEYWORDS = listOf("{","-->","<--")
    override fun log(message: String) {
        val includeKeyword = KEYWORDS.any { message.contains(it, true) }

        if (!includeKeyword) return
        val messageModel = MessagesModel(
            messageLogCode = MessageLogCode.MC001,
            eventType = "CoreApi",
            coreMsgArgs = listOf(message)
        )
        try {
            CoroutineScope(Dispatchers.IO).launch {
                errorLogRepository.sendMessageLog(messageModel)
            }

        } catch (ex: Exception) {
            Timber.e(ex, "Failed to send Log to Core CMS")
        }
    }
}