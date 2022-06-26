package com.nereus.craftbeer.model

data class LoginRequest(
    val hardwareCode: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val accessToken: String,
    val shopId: String,
    val companyId: String,
    val hardwareName: String
)