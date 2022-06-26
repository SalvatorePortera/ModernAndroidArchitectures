package com.nereus.craftbeer.model

data class Device(
    val shopId: String,
    val companyId: String,
    val hardwareCode: String,
    val hardwareName: String
)

data class DeviceList(
    val data: List<Device>
)

data class UpdatePassRequest(
    val hardwareCode: String,
    val newPassword: String,
    val password: String
)