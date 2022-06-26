/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nereus.craftbeer.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.repository.ShopRepository
import com.nereus.craftbeer.util.getShopInfoPref
import com.nereus.craftbeer.util.makeStatusNotification
import retrofit2.HttpException
import timber.log.Timber

/**
 * Sync shop info worker
 *
 * @property shopRepository
 * @constructor
 *
 * @param ctx
 * @param params
 */
class SyncShopInfoWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val shopRepository: ShopRepository
) : CoroutineWorker(ctx, params) {
    companion object {
        const val WORK_NAME = "com.nereus.craftbeer.worker.SyncShopInfoWorker"
    }

    /**
     * Do work
     *
     * @return
     */
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Synchronizing shop info ...", appContext)
        val companyId = inputData.getString(COMPANY_ID) ?: CommonConst.EMPTY_STRING
        val shopId = inputData.getString(SHOP_ID) ?: CommonConst.EMPTY_STRING
        return try {
            if (runAttemptCount > WORKER_RETRY_LIMIT) {
                Timber.d("--- Too many failed attemp, give up")
                makeStatusNotification("Synchronizing shop info failed after 3 retries", appContext)
                return Result.failure()
            }

            if (companyId.isEmpty()) {
                Timber.d("---- COMPANY ID is empty")
                throw IllegalStateException()
            }
            if (shopId.isEmpty()) {
                Timber.d("---- SHOP_ID is empty")
                throw IllegalStateException()
            }

            val company = shopRepository.getCompany(companyId)

            val shop = shopRepository.getShop(shopId)



            storeToPreferences(company, shop)

            makeStatusNotification("Synchronizing shop info successfully", appContext)
            Result.success()
        } catch (e: HttpException) {
            Timber.d("---- Retry Synchronizing shop info")
            makeStatusNotification("Retry Synchronizing shop info", appContext)
            Result.retry()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error sync shop info from core server")
            makeStatusNotification("Error sync shop info from core server", appContext)
            Result.failure()
        }
    }

    /**
     * Store to preferences
     *
     * @param company
     * @param shop
     */
    private fun storeToPreferences(
        company: Company,
        shop: ShopInfo
    ) {
        val prefEdit = getShopInfoPref().edit()
        prefEdit.putString(SHARED_PREF_COMPANY_NAME, company.companyName)
        prefEdit.putString(SHARED_PREF_COMPANY_ID, company.id)
        prefEdit.putString(SHARED_PREF_COMPANY_CODE, company.companyCode)
        prefEdit.putString(SHARED_PREF_SHOP_NAME, shop.shopName)
        prefEdit.putString(SHARED_PREF_SHOP_ID, shop.id)
        prefEdit.putString(SHARED_PREF_SHOP_CODE, shop.shopCode)
        prefEdit.putString(SHARED_PREF_SHOP_ADDRESS, shop.address)
        prefEdit.putString(SHARED_PREF_SHOP_PHONE, shop.phoneNumber)
        prefEdit.putString(SHARED_PREF_SHOP_POSTAL_CODE, shop.postalCode)
        prefEdit.putString(SHARED_PREF_RECEIPT_HEADER, shop.receiptHeader)
        prefEdit.putString(SHARED_PREF_RECEIPT_FOOTER, shop.receiptFooter)
        prefEdit.putString(SHARED_PREF_RECEIPT_LOGO1_URL, shop.logo1Url)
        prefEdit.putString(SHARED_PREF_RECEIPT_LOGO2_URL, shop.logo2Url)
        prefEdit.putString(SHARED_PREF_RECEIPT_TAX_STAMP, shop.receiptStamp)
        prefEdit.putInt(SHARED_PREF_BEER_POURING_CORRECTION_AMOUNT, shop.beerPouringErrorCorrectionValue)
        prefEdit.apply()
    }
}

