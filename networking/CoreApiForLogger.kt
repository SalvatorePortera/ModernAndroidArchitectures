package com.nereus.craftbeer.networking

import androidx.lifecycle.LiveData
import com.nereus.craftbeer.constant.DEFAULT_API_PAGE
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.obniz.ObnizData
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


/**
 * Core api for logger
 *
 * @constructor Create empty Core api for logger
 */
interface CoreApiForLogger {

    /**
     * Create error log
     *
     * @param token
     * @param request
     * @return
     */
    @POST("error-logs")
    suspend fun createErrorLog(
        @Header("Authorization") token: String,
        @Body request: ErrorLogCreateRequest
    ): Response<ResponseBody>

    /**
     * Create log
     *
     * @param token
     * @param request
     * @return
     */
    @POST("event-logs")
    suspend fun createLog(
        @Header("Authorization") token: String,
        @Body request: LogCreateRequest
    ): Response<ResponseBody>

}
