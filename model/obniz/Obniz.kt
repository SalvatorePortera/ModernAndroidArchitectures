package com.nereus.craftbeer.model.obniz

/**
 * Obniz data
 *
 * @property data
 * コンストラクタ  Obniz data
 */
data class ObnizData(
    val data: List<ObnizInfo>
)

/**
 * Obniz info
 *
 * @property id
 * @property updatedAt
 * @property deletedAt
 * @property serverCode
 * @property obnizId
 * @property validFlag
 * @property isOnline
 * @property startUsingAt
 * @property shopId
 * @property companyId
 * @property createdAt
 * コンストラクタ  Obniz info
 */
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


