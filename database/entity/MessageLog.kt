package com.nereus.craftbeer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nereus.craftbeer.model.ErrorLogCreateRequest
import com.nereus.craftbeer.model.LogCreateRequest
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Message log
 *
 * @property eventTime
 * @property obnizID
 * @property deviceCode
 * @property message
 * @property eventType
 * @constructor Create empty Message log
 */
@Entity(tableName = "message_logs")
data class MessageLog(

    @ColumnInfo(name = "event_time")
    val eventTime: LocalDateTime,

    @ColumnInfo(name = "obniz_id")
    val obnizID: String?,

    @ColumnInfo(name = "device_code")
    val deviceCode: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "event_type")
    val eventType: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_log_id")
    var messageLogId: Long = 0L
}

fun MessageLog.asMessageLogRequest(): LogCreateRequest {
    return LogCreateRequest(
        eventTime = eventTime.toISOString(),
        obnizID = obnizID,
        message = message,
        deviceCode = deviceCode,
        eventType = eventType
    )
}
