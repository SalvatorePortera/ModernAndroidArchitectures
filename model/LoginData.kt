package com.nereus.craftbeer.model

/**
 * Login request
 *
 * @property hardwareCode
 * @property password
 * コンストラクタ  Login request
 */
data class LoginRequest(
    val hardwareCode: String,
    val password: String
)

/**
 * Login response
 *
 * @property id
 * @property accessToken
 * @property shopId
 * @property companyId
 * @property hardwareName
 * コンストラクタ  Login response
 */
data class LoginResponse(
    val id: String,
    val accessToken: String,
    val shopId: String,
    val companyId: String,
    val hardwareName: String
)