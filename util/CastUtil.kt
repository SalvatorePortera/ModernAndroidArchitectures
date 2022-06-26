package com.nereus.craftbeer.util

import com.nereus.craftbeer.constant.*
import timber.log.Timber
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

fun <T> castToT(obj: Any?, default: T? = null): T? {
    return try {
        if (obj == null) {
            return default
        }
        obj as T
    } catch (ex: ClassCastException) {
        Timber.e(ex)
        return default
    }
}

fun LocalDateTime.toReceiptDateTimeString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(RECEIPT_TIMESTAMP_FORMAT)
    return return try {
        format(formatter)
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

fun LocalDateTime?.toDisplayedDateTimeString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DISPLAYED_TIMESTAMP_FORMAT)
    return try {
        this?.format(formatter) ?: EMPTY_STRING
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

fun Calendar.toDisplayedDateTimeString(): String {
    return return try {
        LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault()).toDisplayedDateTimeString()
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

fun LocalDateTime.toISOString(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_TIMESTAMP_FORMAT)
    return try {
        format(formatter)
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

fun String.parseISODateTime(): LocalDateTime? {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_TIMESTAMP_FORMAT)
    return try {
        LocalDateTime.parse(this, formatter)
    } catch (ex: DateTimeParseException) {
        Timber.e(ex)
        null
    }
}

fun LocalDateTime.toPointPlusDateFormat(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(POINT_PLUS_YMD_FORMAT)
    return try {
        format(formatter)
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

fun LocalDateTime.toPointPlusTimeFormat(): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(POINT_PLUS_HMS_FORMAT)
    return try {
        format(formatter)
    } catch (ex: DateTimeException) {
        Timber.e(ex)
        EMPTY_STRING
    }
}

operator fun Int.not(): Int {
    return if (this == 0) return 1 else 0
}

fun Short.toBoolean(): Boolean {
    return this != 0.toShort()
}

fun LocalDateTime.toBaseDateTime(): LocalDateTime {
    return atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()
}

fun LocalDateTime.toDeviceDateTime(): LocalDateTime {
    return atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
}
