package com.nereus.craftbeer.repository

import com.nereus.craftbeer.R
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.Device
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.networking.ApiErrorResponse
import com.nereus.craftbeer.networking.ApiResponse
import com.nereus.craftbeer.networking.ApiSuccessResponse
import com.nereus.craftbeer.networking.CoreApiWithoutLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Shop repository
 *
 * @property apiService
 * @constructor Create empty Shop repository
 */
class ShopRepository
@Inject constructor(
    private val apiService: CoreApiWithoutLiveData
) {

    /**
     * Get company
     *
     * @param companyId
     * @return
     */
    suspend fun getCompany(companyId: String): Company {
        val company = withContext(Dispatchers.IO) {
            apiService.getCompanyInfo(
                token = AuthRepository.getAccessToken(),
                companyId = companyId
            )
        }

        return when (val response = ApiResponse.create(company)) {
            is ApiSuccessResponse -> {
                response.body
            }
            is ApiErrorResponse -> {
                throw MessageException(MessagesModel(listOf(response.body.message.toString())))
            }
            else -> {
                throw MessageException(MessagesModel(listOf("Empty response")))
            }
        }
    }

    /**
     * Get shop
     *
     * @param shopId
     * @return
     */
    suspend fun getShop(shopId: String): ShopInfo {
        val shop = withContext(Dispatchers.IO) {
            apiService.getShopInfo(token = AuthRepository.getAccessToken(), shopId = shopId)
        }

        return when (val response = ApiResponse.create(shop)) {
            is ApiSuccessResponse -> {
                response.body
            }
            is ApiErrorResponse -> {
                throw MessageException(MessagesModel(listOf(response.body.message.toString())))
            }
            else -> {
                throw MessageException(MessagesModel(listOf("Empty response")))
            }
        }
    }

    /**
     * Get devices
     *
     * @param companyCode
     * @param shopCode
     * @return
     */
    suspend fun getDevices(companyCode: String, shopCode: String): List<Device> {
        if (shopCode.isBlank() || companyCode.isBlank()) {
            throw MessageException(MessagesModel(R.string.msg_internal_exception))
        }

        val shop = withContext(Dispatchers.IO) {
            apiService.getDevices(shopCode, companyCode)
        }

        return when (val response = ApiResponse.create(shop)) {
            is ApiSuccessResponse -> {
                response.body.data
            }
            is ApiErrorResponse -> {
                throw MessageException(MessagesModel(listOf(response.body.message.toString())))
            }
            else -> {
                throw MessageException(MessagesModel(listOf("Empty response")))
            }
        }
    }


}
