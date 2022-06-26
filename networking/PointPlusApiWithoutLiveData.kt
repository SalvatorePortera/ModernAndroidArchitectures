package com.nereus.craftbeer.networking

import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardRequestTransactions
import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardResponseTransactions
import retrofit2.Response
import retrofit2.http.*


interface PointPlusApiWithoutLiveData {
    /* Call transactions*/
    @POST(".")
    suspend fun callCardApi(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String
    ): Response<BasedCardResponseTransactions>

    /* Confirm 2-phases transactions */
    @POST(".")
    suspend fun confirmCardApi(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String,
        @Query("actionName") actionName: String = "TransactionConfirm"
    ): Response<BasedCardResponseTransactions>

    /* Transaction cancellation */
    @POST(".")
    suspend fun cancelTransaction(
        @Body request: BasedCardRequestTransactions,
        @Query("com_key") companyKey: String,
        @Query("actionName") actionName: String = "AutoTransactionCancel"
    ): Response<BasedCardResponseTransactions>
}
