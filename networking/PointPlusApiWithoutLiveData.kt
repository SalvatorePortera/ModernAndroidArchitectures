package com.nereus.craftbeer.networking

import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardRequestTransactions
import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardResponseTransactions
import retrofit2.Response
import retrofit2.http.*


/**
 * Point plus api without live data
 *
 * コンストラクタ  Point plus api without live data
 */
interface PointPlusApiWithoutLiveData {
    /**
     * Call card api
     * Call transactions
     * @param request
     * @param companyKey
     * @return
     */
    @POST(".")
    suspend fun callCardApi(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String
    ): Response<BasedCardResponseTransactions>

    /**
     * Confirm card api
     * Confirm 2-phases transactions
     * @param request
     * @param companyKey
     * @param actionName
     * @return
     */
    @POST(".")
    suspend fun confirmCardApi(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String,
        @Query("actionName") actionName: String = "TransactionConfirm"
    ): Response<BasedCardResponseTransactions>

    /**
     * Cancel transaction
     * Transaction cancellation
     * @param request
     * @param companyKey
     * @param actionName
     * @return
     */
    @POST(".")
    suspend fun cancelTransaction(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String,
        @Query("actionName") actionName: String = "AutoTransactionCancel"
    ): Response<BasedCardResponseTransactions>
}
