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
import android.os.AsyncTask
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.constant.WORKER_RETRY_LIMIT
import com.nereus.craftbeer.realm.RealmApplication
import com.nereus.craftbeer.util.Downloader
import com.nereus.craftbeer.util.makeStatusNotification
import retrofit2.HttpException
import timber.log.Timber
import java.io.File

class UpdateBeerWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(ctx, params) {
    companion object {
        const val WORK_NAME = "com.nereus.craftbeer.worker.UpdateBeerWorker"
        var countDownloadFile = 0

        fun checkFile(): Boolean {
            var isExist = true
            val color = arrayListOf("yellow", "red", "black", "white", "orange", "sour", "highball")
            for (item in color) {
                val file = File(
                    RealmApplication.instance.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/",
                    "$item.mp4"
                )
                if (!file.exists()) {
                    isExist = false
                }
                break
            }
            return isExist
        }
    }

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Updating Beer Data ...", appContext)
        return try {
            if (runAttemptCount > WORKER_RETRY_LIMIT) {
                Timber.d("--- Too many failed attempts, give up")
                makeStatusNotification("Updating beer data failed after 3 retries", appContext)
                return Result.failure()
            }
            downloadBeerVideo()
            makeStatusNotification("Updating beer data please wait", appContext)
            Result.success()
        } catch (e: HttpException) {
            Timber.d("---- Retry updating beer data")
            makeStatusNotification("Retry updating beer data", appContext)
            Result.retry()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error updating beer data from core server")
            makeStatusNotification("Error updating beer data from core server", appContext)
            Result.failure()
        }
    }


    private fun downloadBeerVideo() {
        deleteInternalStorageVideo()
        checkFolder(context = applicationContext)
        val array = arrayListOf(
            BuildConfig.YELLOW_BEER,
            BuildConfig.RED_BEER,
            BuildConfig.WHITE_BEER,
            BuildConfig.BLACK_BEER,
            BuildConfig.ORANGE_BEER,
            BuildConfig.SOUR_BEER,
            BuildConfig.HIGHBALL_BEER
        )
        for (item in array) {
            val downloader = Downloader(contexts = applicationContext)
            downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item)
        }
    }
}

fun checkFolder(context: Context) {
    val path: String =
        context.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/"
    val dir = File(path)
    var isDirectoryCreated: Boolean = dir.exists()
    if (!isDirectoryCreated) {
        isDirectoryCreated = dir.mkdir()
    }
    if (isDirectoryCreated) {
        Timber.d("FILE ALREADY CREATED")
    }
}

fun deleteInternalStorageVideo() {
    val color = arrayListOf("yellow", "red", "black", "white", "orange", "sour", "highball")
    for (item in color) {
        val file = File(
            RealmApplication.instance.getExternalFilesDir(null)?.absolutePath.toString() + "/DownloadedVideo/",
            "$item.mp4"
        )
        if (file.exists()) {
            file.delete()
        }
    }
}

