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
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.constant.WORKER_RETRY_LIMIT
import com.nereus.craftbeer.repository.GoodsRepository
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

/**
 * Sync goods to database worker
 *
 * @property goodsRepository
 * @constructor
 *
 * @param ctx
 * @param params
 */
class SyncGoodsToDatabaseWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val goodsRepository: GoodsRepository
) : CoroutineWorker(ctx, params) {
    companion object {
        const val WORK_NAME = "com.nereus.craftbeer.worker.SyncGoodsToDatabaseWorker"
    }

    /**
     * Do work
     *
     * @return
     */
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Synchronizing goods ...", appContext)
        val token = inputData.getString(ACCESS_TOKEN) ?: CommonConst.EMPTY_STRING
        return try {
            if (runAttemptCount > WORKER_RETRY_LIMIT) {
                Timber.d("--- Too many failed attemp, give up")
                makeStatusNotification("Synchronizing goods failed after 3 retries", appContext)
                return Result.failure()
            }
            if (token.isEmpty()) {
                Timber.d("---- ACCESS_TOKEN is empty")
                throw IllegalStateException()
            }

            withContext(Dispatchers.IO) {
                goodsRepository.syncGoods(token)
            }
            makeStatusNotification("Synchronizing goods successfully", appContext)
            Result.success()

        } catch (e: HttpException) {
            Timber.d("---- Retry Synchronizing goods")
            makeStatusNotification("Retry Synchronizing goods", appContext)
            Result.retry()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error sync goods from core server")
            displayToast(appContext, "Error sync goods from core server")
            makeStatusNotification("Error sync goods from core server", appContext)
            Result.failure()
        }
    }
}
