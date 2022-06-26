package com.nereus.craftbeer.repository

import android.content.Context
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.LoginRequest
import com.nereus.craftbeer.model.LoginResponse
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.UpdatePassRequest
import com.nereus.craftbeer.networking.*
import com.nereus.craftbeer.realm.RealmApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: BeerCraftApi,
    private val coreApiWithoutLiveData: CoreApiWithoutLiveData
) {
    companion object {
        fun getAccessToken(): String {
            return RealmApplication.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
                .getString(ACCESS_TOKEN, NO_TOKEN) ?: CommonConst.EMPTY_STRING
        }
        fun getDeviceId(): String {
            return RealmApplication.instance.getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
                .getString(PREF_DEVICE_ID, EMPTY_STRING) ?: CommonConst.EMPTY_STRING
        }
    }

    suspend fun checkLogin(request: LoginRequest): LoginResponse {
        val result = withContext(Dispatchers.IO) {
            coreApiWithoutLiveData.checkLogin(request)
        }
        when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {
                return response.body
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(listOf(response.body.message.toString())))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }
    }

    suspend fun changeDevicePassword(request: UpdatePassRequest): ResponseBody {
        val result = withContext(Dispatchers.IO) {
            coreApiWithoutLiveData.changeDevicePass(request)
        }
        when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {
                return response.body
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(listOf(response.body.message.toString())))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }
    }
}