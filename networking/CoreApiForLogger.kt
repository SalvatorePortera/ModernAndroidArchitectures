package com.nereus.craftbeer.networking

import androidx.lifecycle.LiveData
import com.nereus.craftbeer.constant.DEFAULT_API_PAGE
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.obniz.ObnizData
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface CoreApiForLogger {

    @POST("error-logs")
    suspend fun createErrorLog(
        @Header("Authorization") token: String,
        @Body request: ErrorLogCreateRequest
    ): Response<ResponseBody>

    @POST("event-logs")
    suspend fun createLog(
        @Header("Authorization") token: String,
        @Body request: LogCreateRequest
    ): Response<ResponseBody>

}
