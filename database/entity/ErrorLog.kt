package com.nereus.craftbeer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nereus.craftbeer.model.ErrorLogCreateRequest
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Error log
 *
 * @property errorCode
 * @property obnizID
 * @property deviceCode
 * @property message
 * @property occurredAt
 * @constructor Create empty Error log
 */
@Entity(tableName = "error_logs")
data class ErrorLog(

    @ColumnInfo(name = "error_code")
    val errorCode: String,

    @ColumnInfo(name = "obniz_id")
    val obnizID: String?,

    @ColumnInfo(name = "device_code")
    val deviceCode: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "occurred_at")
    val occurredAt: LocalDateTime
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "error_log_id")
    var errorLogId: Long = 0L
}

fun ErrorLog.asErrorLogCreateRequest(): ErrorLogCreateRequest {
    return ErrorLogCreateRequest(
        errorCode = errorCode,
        obnizID = obnizID,
        message = message,
        deviceCode = deviceCode,
        occurredAt = occurredAt.toISOString()
    )
}
