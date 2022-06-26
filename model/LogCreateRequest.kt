package com.nereus.craftbeer.model

data class LogCreateRequest(
    val eventTime: String,

    val obnizID: String?,

    val deviceCode: String,

    val message: String,

    val eventType: String
)


