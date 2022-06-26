package com.nereus.craftbeer.model.obniz

data class ObnizData(
    val data: List<ObnizInfo>
)

data class ObnizInfo(
    val id: String,
    val updatedAt: String,
    val deletedAt: String?,
    val serverCode: String,
    val obnizId: String,
    val validFlag: Int,
    val isOnline: Int,
    val startUsingAt: String?,
    val shopId: String,
    val companyId: String,
    val createdAt: String
)


