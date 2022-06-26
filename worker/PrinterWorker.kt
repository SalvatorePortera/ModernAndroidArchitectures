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
import com.nereus.craftbeer.constant.ACCESS_TOKEN
import com.nereus.craftbeer.constant.RECEIPT_LIST
import com.nereus.craftbeer.constant.WORKER_RETRY_LIMIT
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.repository.ShopRepository
import com.nereus.craftbeer.util.makeStatusNotification
import com.nereus.craftbeer.util.printer.getReceiptGenerator
import retrofit2.HttpException
import timber.log.Timber

class PrinterWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val shopRepository: ShopRepository
) : CoroutineWorker(ctx, params) {
    companion object {
        const val WORK_NAME = "com.nereus.craftbeer.worker.PrinterWorker"
    }

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("--- Printing receipt...", appContext)
        val token = inputData.keyValueMap.get(ACCESS_TOKEN) as String
        val receipts = inputData.keyValueMap.get(RECEIPT_LIST) as List<Receipt>
        return try {
            if (runAttemptCount > WORKER_RETRY_LIMIT) {
                Timber.d("--- Too many failed attemp, give up")
                makeStatusNotification("Printing receipt failed after 3 retries", appContext)
                return Result.failure()
            }
            if (token.isEmpty()) {
                Timber.d("---- ACCESS_TOKEN is empty")
                throw IllegalStateException()
            }

            receipts.forEach {
                val company = shopRepository.getCompany(it.companyId)

                val shop = shopRepository.getShop(it.shopId)

                it.getReceiptGenerator(companyInfo = company, shopInfo = shop)
                    .generate()
            }

            makeStatusNotification("Printing receipt successfully", appContext)
            Result.success()
        } catch (e: HttpException) {
            Timber.d("---- Retry Printing receipt")
            makeStatusNotification("Retry Printing receipt", appContext)
            Result.retry()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error Printing receipt")
            makeStatusNotification("Error Printing receipt", appContext)
            Result.failure()
        }
    }
}
