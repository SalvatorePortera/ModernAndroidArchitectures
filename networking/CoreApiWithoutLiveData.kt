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
 * Core api without live data
 *
 * @constructor Create empty Core api without live data
 */
interface CoreApiWithoutLiveData {
    // Login for devices
    @POST("/auth/device/login")
    suspend fun checkLogin(@Body loginData: LoginRequest): Response<LoginResponse>

    /**
     * Check login1
     *
     * @param loginData
     * @return
     */
    @POST("/auth/device/login")
    suspend fun checkLogin1(@Body loginData: LoginRequest): LiveData<ApiResponse<LoginResponse>>

    /**
     * Change device pass
     * Change Device's Password
     * @param changePassData
     * @return
     */
    @POST("auth/device/change-password")
    suspend fun changeDevicePass(
        @Body changePassData: UpdatePassRequest
    ): Response<ResponseBody>

    /**
     * Get devices
     * Get Devices List ID
     * @param shopCode
     * @param companyCode
     * @return
     */
    @GET("/devices/search")
    suspend fun getDevices(
        @Query("shop_code") shopCode: String,
        @Query("company_code") companyCode: String
    ): Response<DeviceList>


    // Change Device's Password
    @POST("auth/device/change-password")
    fun changeDevicePass(
        @Body changePassData: SaleLogCreateRequest
    ): LiveData<ApiResponse<ResponseBody>>

    // Get Goods List
    @GET("/goods-shop/device")
    fun getGoodsList(@Header("Authorization") token: String): Observable<Data>

    // Get Goods List
    @GET("/goods/device")
    suspend fun getGoodsList1(@Header("Authorization") token: String): Response<Data>

    // Search Goods
    @GET("/goods/search")
    suspend fun search(@Header("Authorization") token: String,
                       @Query("query") query: String): Response<Data>

    // Get sale log by poin_plus_id
    @GET("/sale-logs/sales")
    suspend fun getSaleLogs(
        @Header("Authorization") token: String,
        @Query("point_plus_id") pointPlusId: String? = null,
        @Query("startTime") startTime: String? = null,
        @Query("endTime") endTime: String? = null,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 0,
    ): Response<SaleLogData>

    // Get top up by poin_plus_id
    @GET("/topup/by-point-plus-id")
    suspend fun getTopUps(
        @Header("Authorization") token: String,
        @Query("point_plus_id") pointPlusId: String? = null,
        @Query("startTime") startTime: String? = null,
        @Query("endTime") endTime: String? = null,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 0,
    ): Response<TopUpData>

    // Update goods's status
    @PATCH("/goods-shop/{id}/handling-flag")
    fun updateGoodsStatus(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body handlingFlag: UpdateGoodsStatusRequest
    ): Observable<ResponseBody>

    @POST("sale-logs")
    suspend fun createSaleLog(
        @Header("Authorization") token: String,
        @Body request: SaleLogCreateRequest
    ): Response<SaleLog>

    @PATCH("/sale-logs/count-printed/{saleLogId}")
    suspend fun setPrintedSalelog(
        @Path("saleLogId") saleLogId: String, @Header("Authorization") token: String
    ): Response<ResponseBody>

    @PATCH("/sale-logs/set-issued/{id}")
    suspend fun setIssuedSalelog(
        @Path("id") saleLogId: String, @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("topup")
    suspend fun createTopUp(
        @Header("Authorization") token: String,
        @Body request: TopUpCreateRequest
    ): Response<TopUp>

    @PATCH("/topup/count-printed/{id}")
    suspend fun setPrintedTopup(
        @Path("id") topupId: String, @Header("Authorization") token: String
    ): Response<ResponseBody>

    @PATCH("/topup/set-issued/{id}")
    suspend fun setIssuedTopup(
        @Path("id") topupId: String, @Header("Authorization") token: String
    ): Response<ResponseBody>
    // Get Company Info
    @GET("/companies/{companyId}/device")
    suspend fun getCompanyInfo(
        @Header("Authorization") token: String,
        @Path("companyId") companyId: String
    ): Response<Company>

    // Get Shop Info
    @GET("/shops/{shopId}/device")
    suspend fun getShopInfo(
        @Header("Authorization") token: String,
        @Path("shopId") shopId: String
    ): Response<ShopInfo>

    // Get Tap Beer Server Device
    @GET("/tap-beer-server/device")
    suspend fun getTabBeersServerList(@Header("Authorization") token: String): Response<BeerData>

    // Update beer's status
    @PATCH("/tap-beer-server/{id}/maintain-flag")
    suspend fun updateBeersStatus(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body maintainFlag: UpdateBeersStatusRequest
    ): Response<ResponseBody>

    // Get Obniz server list
    @GET("/beer-servers/device")
    suspend fun getObnizServerList(@Header("Authorization") token: String): Response<ObnizData>

    // Get Tap Beer Shop
    @GET("/tap-beers-shop/device")
    suspend fun getTapBeerShop(@Header("Authorization") token: String): Response<BeerData>

    // Get Tap Beer Device
    @GET("/tap-beers/device")
    suspend fun getTapBeerDevice(@Header("Authorization") token: String): Response<TabBeerDeviceData>

    // Update tap beer server
    @PATCH("/tap-beer-server/{id}/device")
    suspend fun updateTapBeerServer(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body beerServerUpdateModel: UpdateTapBeerServerRequest
    ): Response<ResponseBody>

    // lock card
    @PATCH("/lock-card/lock/{pointPlusId}")
    suspend fun lockCard(
        @Path("pointPlusId") pointPlusId: String, @Header("Authorization") token: String
    ): Response<LockCardStatus>

    // unlock card
    @PATCH("/lock-card/unlock/{pointPlusId}")
    suspend fun unlockCard(
        @Path("pointPlusId") pointPlusId: String, @Header("Authorization") token: String
    ): Response<LockCardStatus>

    // Get Tap Beer Shop
    @GET("/lock-card/{pointPlusId}")
    suspend fun getLockCardStatus(
        @Path("pointPlusId") pointPlusId: String,
        @Header("Authorization") token: String
    ): Response<LockCardStatus>
}
