package com.nereus.craftbeer.repository

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.PREF_DEVICE_CODE
import com.nereus.craftbeer.database.dao.ErrorLogDao
import com.nereus.craftbeer.database.entity.ErrorLog
import com.nereus.craftbeer.database.entity.MessageLog
import com.nereus.craftbeer.database.entity.asErrorLogCreateRequest
import com.nereus.craftbeer.database.entity.asMessageLogRequest
import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.networking.*
import com.nereus.craftbeer.util.getDeviceInfoPref
import com.nereus.craftbeer.util.toBaseDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class ErrorLogRepository
@Inject constructor(
    private val apiService: CoreApiForLogger,
    private val errorLogDao: ErrorLogDao
) {

    /**
     * Send Error Log to core server, save to tablet DB if sending failed
     */
    suspend fun sendErrorLog(messageModel: MessagesModel) {
        var errorLog: ErrorLog? = null
        try {
            errorLog = ErrorLog(
                errorCode = messageModel.getErrorLogCode()!!.name,
                deviceCode = getDeviceInfoPref().getString(PREF_DEVICE_CODE, EMPTY_STRING)!!,
                obnizID = messageModel.getObnizId(),
                message = messageModel.getCoreMessage(),
                occurredAt = LocalDateTime.now().toBaseDateTime()
            )
            val messagesModel = MessagesModel()
            sendErrorLog(errorLog, messagesModel)
            if (messagesModel.hasMessage()) {
                // Send failed, store to DB to resend later
                Timber.e(messagesModel.getMessages().toString())
                saveErrorLog(errorLog)
            }
        } catch (ex: Exception) {
            errorLog?.let {
                Timber.e(
                    ex,
                    "Failed to send and save Error Log: [Error Code: %s][ObnizID: %s][Message: %s]",
                    errorLog.errorCode,
                    errorLog.obnizID,
                    errorLog.message
                )
            } ?: Timber.e(
                ex, "Failed to send and save Error Log: [Error Code: %s][ObnizID: %s]",
                messageModel.getErrorLogCode()!!.name, messageModel.getObnizId()
            )
        }
    }


    suspend fun sendMessageLog(messageModel: MessagesModel) {
        var messageLog: MessageLog? = null
        try {
            messageLog = MessageLog(
                eventTime = LocalDateTime.now().toBaseDateTime(),
                deviceCode = getDeviceInfoPref().getString(PREF_DEVICE_CODE, EMPTY_STRING)!!,
                obnizID = messageModel.getObnizId(),
                message = messageModel.getCoreMessage(),
                eventType = messageModel.getEventType()
            )
            val messagesModel = MessagesModel()
            sendLog(messageLog, messagesModel)
            if (messagesModel.hasMessage()) {
                Timber.e(messagesModel.getMessages().toString())
            }
        } catch (ex: Exception) {
            messageLog?.let {
                Timber.e(
                    ex,
                    "Failed to send and save Error Log: [Error Code: %s][ObnizID: %s][Message: %s]",
                    messageLog.deviceCode,
                    messageLog.obnizID,
                    messageLog.message
                )
            } ?: Timber.e(
                ex, "Failed to send and save Error Log: [Error Code: %s][ObnizID: %s]",
                messageModel.getEventType(), messageModel.getObnizId()
            )
        }
    }

    /**
     * Send Unsync Error Logs to core server in case had been failed to send before
     */
    suspend fun sendErrorLogs() {
        try {
            val saleLogs = withContext(Dispatchers.IO) {
                getUnsyncErrorLogs()
            }
            sendErrorLogs(saleLogs)
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_sync_sale_log_failed)
            )
        }
    }

    private suspend fun saveErrorLog(
        errorLog: ErrorLog
    ) {
        withContext(Dispatchers.IO) {
            errorLogDao.insert(errorLog)
        }
    }

    private suspend fun getUnsyncErrorLogs(): List<ErrorLog> {
        Timber.d("---------------- getUnsyncErrorLogs")
        return try {
            withContext(Dispatchers.IO) {
                errorLogDao.getAll()
            }
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_load_error_log_failed)
            )
        }
    }

    private suspend fun sendErrorLogs(errorLogs: List<ErrorLog>) {

        if (errorLogs.isEmpty()) {
            return
        }

        val messagesModel = MessagesModel()

        errorLogs.forEach {
            sendErrorLog(it, messagesModel)
        }

        if (messagesModel.hasMessage()) {
            throw MessageException(messagesModel)
        }

    }

    private suspend fun sendErrorLog(
        it: ErrorLog,
        messagesModel: MessagesModel
    ) {
        val result = withContext(Dispatchers.IO) {
            apiService.createErrorLog(AuthRepository.getAccessToken(), it.asErrorLogCreateRequest())
        }

        when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {
                errorLogDao.delete(it)
            }
            is ApiErrorResponse -> {
                messagesModel.addTabletMessage(response.body.message.toString())
            }
            else -> {
                messagesModel.addTabletMessage(R.string.msg_internal_exception)
            }
        }
    }

    private suspend fun sendLog(
        it: MessageLog,
        messagesModel: MessagesModel
    ) {
        val result = withContext(Dispatchers.IO) {
            apiService.createLog(AuthRepository.getAccessToken(), it.asMessageLogRequest())
        }

        when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {
                Timber.d("Api successfully sent")
            }
            is ApiErrorResponse -> {
                messagesModel.addTabletMessage(response.body.message.toString())
            }
            else -> {
                messagesModel.addTabletMessage(R.string.msg_internal_exception)
            }
        }
    }
}
