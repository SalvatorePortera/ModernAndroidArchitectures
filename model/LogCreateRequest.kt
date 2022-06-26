package com.nereus.craftbeer.model

/**
 * Log create request
 *
 * @property eventTime
 * @property obnizID
 * @property deviceCode
 * @property message
 * @property eventType
 * @constructor Create empty Log create request
 */
data class LogCreateRequest(
    val eventTime: String,

    val obnizID: String?,

    val deviceCode: String,

    val message: String,

    val eventType: String
)


