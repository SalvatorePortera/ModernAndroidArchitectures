package com.nereus.craftbeer.model

/**
 * Error log create request
 *
 * @property errorCode
 * @property obnizID
 * @property deviceCode
 * @property message
 * @property occurredAt
 * コンストラクタ  Error log create request
 */
data class ErrorLogCreateRequest(
    val errorCode: String,

    val obnizID: String?,

    val deviceCode: String,

    val message: String,

    val occurredAt: String
)


