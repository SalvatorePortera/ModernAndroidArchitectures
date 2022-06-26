package com.nereus.craftbeer.networking

import androidx.lifecycle.LiveData
import com.nereus.craftbeer.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


/**
 * Beer craft api
 *
 * @constructor Create empty Beer craft api
 */
interface BeerCraftApi {
    // Login for devices

    @POST("/auth/device/login")
    suspend fun checkLogin1(@Body loginData: LoginRequest):  LiveData<ApiResponse<LoginResponse>>

    // Get Devices List ID
    @GET("/devices")
    fun getDevices(
        @Header("Shop-Id") shopId: String,
        @Header("Company-Id") companyId: String
    ): Observable<DeviceList>



    // Get Goods List
    @GET("/goods-shop/device")
    fun getGoodsList(@Header("Authorization") token: String): Observable<Data>

    // Get Goods List
    @GET("/goods-shop/device")
    suspend fun getGoodsList1(@Header("Authorization") token: String): Response<Data>
    
    // Get Beers List
    @GET("/tap-beers-shop/device")
    fun getBeersList(@Header("Authorization") token: String): Observable<BeerData>

    // Get Tap Beer Server Device
    @GET("/tap-beer-server/device")
    fun getTabBeersServerList(@Header("Authorization") token: String): Observable<BeerData>

    // Update goods's status
    @PATCH("/goods-shop/{id}/handling-flag")
    fun updateGoodsStatus(@Path("id") id : String, @Header("Authorization") token : String , @Body handlingFlag : UpdateGoodsStatusRequest):Observable<ResponseBody>

    // Update beer's status
    @PATCH("/tap-beer-server/{id}/maintain-flag")
    fun updateBeersStatus(@Path("id") id : String, @Header("Authorization") token : String , @Body maintainFlag : UpdateBeersStatusRequest):Observable<ResponseBody>




}
