package com.nereus.craftbeer.model

/**
 * Device
 *
 * @property shopId
 * @property companyId
 * @property hardwareCode
 * @property hardwareName
 * @constructor Create empty Device
 */
data class Device(
    val shopId: String,
    val companyId: String,
    val hardwareCode: String,
    val hardwareName: String
)

/**
 * Device list
 *
 * @property data
 * @constructor Create empty Device list
 */
data class DeviceList(
    val data: List<Device>
)

/**
 * Update pass request
 *
 * @property hardwareCode
 * @property newPassword
 * @property password
 * @constructor Create empty Update pass request
 */
data class UpdatePassRequest(
    val hardwareCode: String,
    val newPassword: String,
    val password: String
)