package com.nereus.craftbeer.model

data class ErrorLogCreateRequest(
    val errorCode: String,

    val obnizID: String?,

    val deviceCode: String,

    val message: String,

    val occurredAt: String
)


