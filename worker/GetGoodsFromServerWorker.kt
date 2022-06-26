///*
// * Copyright (C) 2018 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.nereus.craftbeer.worker
//
//import android.content.Context
//import androidx.hilt.Assisted
//import androidx.hilt.work.WorkerInject
//import androidx.lifecycle.Observer
//import androidx.work.*
//import com.nereus.craftbeer.constant.ACCESS_TOKEN
//import com.nereus.craftbeer.constant.CommonConst
//import com.nereus.craftbeer.constant.GOODS_LIST
//import com.nereus.craftbeer.constant.Status
//import com.nereus.craftbeer.controller.BaseController
//import com.nereus.craftbeer.database.dao.SaleLogDao
//import com.nereus.craftbeer.database.entity.SaleLog
//import com.nereus.craftbeer.networking.BeerCraftService
//import com.nereus.craftbeer.repository.AuthRepository
//import com.nereus.craftbeer.repository.GoodsRepository
//import com.nereus.craftbeer.repository.SaleLogRepository
//import com.nereus.craftbeer.util.makeStatusNotification
//import com.nereus.craftbeer.util.sleep
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.withContext
//import timber.log.Timber
//
//open class GetGoodsFromServerWorker @WorkerInject constructor(
//    @Assisted ctx: Context,
//    @Assisted params: WorkerParameters,
//    private val goodsRepository: GoodsRepository,
//    private val authRepository: AuthRepository,
//    private val saleLogRepository: SaleLogRepository,
//    private val saleLogDao: SaleLogDao
//) : CoroutineWorker(ctx, params) {
//
//    override suspend fun doWork(): Result {
//        val appContext = applicationContext
//
//        val token = inputData.getString(ACCESS_TOKEN) ?: CommonConst.EMPTY_STRING
//
//        makeStatusNotification("Synchronizing goods ...", appContext)
//        sleep()
//
////         try {
//
//        if (token.isEmpty()) {
//            println("---- ACCESS_TOKEN is empty")
//            throw IllegalStateException()
//        }
//        println("---- ACCESS_TOKEN is ")
//        println(token)
////        return runBlocking {
//
//
//        val result = saleLogRepository.getAll().value
//        var outputData = workDataOf(GOODS_LIST to result)
//
//           return  Result.success(outputData)
////        }
////            when (result?.status) {
////                Status.SUCCESS -> {
////                    Timber.i("------------------------result SUCCESS")
////                    Timber.i(result.data.toString())
////                    outputData = workDataOf(GOODS_LIST to result)
////
////                }
////                Status.ERROR -> {
////                    Timber.i("------------------------result ERROR")
////                }
////                Status.LOADING -> {
////                    Timber.i("------------------------result LOADING")
////                }
////            }
////            val result1 = saleLogDao.getAll()
////            Timber.i("------------------------result1")
////            Timber.i(result1.value.toString())
////        }
//
////        return Result.success(outputData)
////        return withContext(Dispatchers.IO) {
////            saleLogDao.getAll()?.value.let { result ->
////                if (result == null) {
////                    Timber.i("------------------------result == null")
////                    Result.failure()
////
////                } else {
////                    Timber.i("------------------------result != null")
////
////                    Timber.i(result.toString())
//////            else
//////                when (result.status) {
//////                    Status.SUCCESS -> {
//////
//////                    }
//////                    Status.ERROR -> {
//////
//////                    }
//////                    Status.LOADING -> {
//////                        // ignore
//////                    }
//////                }
////                    val outputData = workDataOf(GOODS_LIST to result)
////                    Result.success(outputData)
////                }
////            }
////        }
//    }
//}
//
//
