package com.nereus.craftbeer.repository

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.DEFAULT_API_LIMIT
import com.nereus.craftbeer.constant.DEFAULT_API_PAGE
import com.nereus.craftbeer.database.dao.SaleLogDao
import com.nereus.craftbeer.database.dao.SaleLogDetailDao
import com.nereus.craftbeer.database.dao.SaleLogListDao
import com.nereus.craftbeer.database.entity.asSaleLogDetailsRequestModel
import com.nereus.craftbeer.database.entity.asSaleLogDetailsRequestModelBeer
import com.nereus.craftbeer.database.entity.asSaleLogRequest
import com.nereus.craftbeer.database.entity.asUnsyncLogs
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.networking.ApiErrorResponse
import com.nereus.craftbeer.networking.ApiResponse
import com.nereus.craftbeer.networking.ApiSuccessResponse
import com.nereus.craftbeer.networking.CoreApiWithoutLiveData
import com.nereus.craftbeer.util.toBaseDateTime
import com.nereus.craftbeer.util.toISOString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class SaleLogRepository
@Inject constructor(
    private val apiService: CoreApiWithoutLiveData,
    private val saleLogDao: SaleLogDao,
    private val saleLogDetailDao: SaleLogDetailDao,
    private val saleLogListDao: SaleLogListDao
) {
    suspend fun saveSaleLog(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        goods: List<CombinationGoodsInfo>
    ) {
        try {
            val saleLogId = withContext(Dispatchers.IO) {
                saleLogDao.insert(saleLog)
            }
            saleLogDetailDao.insertAll(goods.asSaleLogDetailEntities(saleLogId))

            Timber.i("-------- sale log inserted id: %d", saleLogId)

        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    suspend fun saveSaleLog(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        beer: CombinationBeersInfo
    ) {
        try {
            val saleLogId = withContext(Dispatchers.IO) {
                saleLogDao.insert(saleLog)
            }
            saleLogDetailDao.insert(beer.asSaleLogDetailEntity(saleLogId))
            Timber.i("-------- sale log inserted id: %d", saleLogId)
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to store sale log to DB")
        }
    }

    suspend fun setSaleLogsPrinted(
        saleLogId: String
    ) {
        try {
            withContext(Dispatchers.IO) {
                apiService.setPrintedSalelog(
                    saleLogId,
                    AuthRepository.getAccessToken()
                )
            }

        } catch (ex: Exception) {
            Timber.e(ex, "Failed to set SaleLog printed")
        }
    }

    suspend fun setSaleLogsIssued(
        saleLogId: String
    ) {
        try {
            withContext(Dispatchers.IO) {
                apiService.setIssuedSalelog(
                    saleLogId,
                    AuthRepository.getAccessToken()
                )
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to set SaleLog Issued")
        }
    }

    suspend fun getUnsyncSaleLogs(): List<UnsyncLog> {
        Timber.d("---------------- getUnsyncSaleLogs")
        return try {
            withContext(Dispatchers.IO) {
                saleLogDao.getAll()
            }.asUnsyncLogs()
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_load_sale_log_failed)
            )
        }
    }

    suspend fun getMany(
        pointPlusId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        page: Int = DEFAULT_API_PAGE,
        limit: Int = DEFAULT_API_LIMIT
    ): List<SaleLog> {
        Timber.d("---------------- getSaleLogsByPointPlusId")
        return try {
            val saleLogs = withContext(Dispatchers.IO) {
                apiService.getSaleLogs(
                    AuthRepository.getAccessToken(),
                    pointPlusId,
                    startTime?.toBaseDateTime()?.toISOString(),
                    endTime?.toBaseDateTime()?.toISOString(),
                    page = page,
                    limit = limit
                )
            }
            when (val response = ApiResponse.create(saleLogs)) {
                is ApiSuccessResponse -> {
                    response.body.data
                }
                is ApiErrorResponse -> {
                    throw IllegalStateException(response.body.message.toString())
                }
                else -> {
                    throw IllegalStateException("Empty response")
                }
            }
        } catch (e: Exception) {
            throw MessageException(
                cause = e,
                messagesModel = MessagesModel(R.string.msg_internal_exception)
            )
        }
    }

    suspend fun sendSaleLogs() {
        try {
            val saleLogs = withContext(Dispatchers.IO) {
                getUnsyncSaleLogs()
            }.asSaleLogRequests()
            sendSaleLogs(saleLogs)
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_sync_sale_log_failed)
            )
        }
    }

    suspend fun sendSaleLogs(saleLogs: List<SaleLogCreateRequest>) {

        if (saleLogs.isEmpty()) {
            return
        }
        val salelogWithDetails = withContext(Dispatchers.IO) {
            saleLogListDao.getSaleLogWithDetails()
        }

        val messagesModel = MessagesModel()

        saleLogs.forEach {
            it.saleLogDetails = salelogWithDetails.filter { it1 ->
                it.saleLog!!.saleLogId == it1.saleLog.saleLogId
            }.first().saleList.asSaleLogDetailsRequestModel()

            val result = withContext(Dispatchers.IO) {
                apiService.createSaleLog(AuthRepository.getAccessToken(), it)
            }

            when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    it.saleLog?.let { it1 -> saleLogDao.delete(it1) }
                }
                is ApiErrorResponse -> {
                    messagesModel.addTabletMessage(response.body.message.toString())
                }
                else -> {
                    messagesModel.addTabletMessage(R.string.msg_internal_exception)
                }
            }
        }

        if (messagesModel.hasMessage()) {
            throw MessageException(messagesModel)
        }

    }

    suspend fun sendSaleLog(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        goods: List<CombinationGoodsInfo>
    ): String {
        val request = saleLog.asSaleLogRequest()

        request.saleLogDetails = goods.asSaleLogDetailEntities(0L).asSaleLogDetailsRequestModel()

        try {
            val result = withContext(Dispatchers.IO) {
                apiService.createSaleLog(AuthRepository.getAccessToken(), request)
            }

            when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    return response.body.id
                }
                is ApiErrorResponse -> {
                    throw MessageException(MessagesModel(response.body.message.toString()))
                }
                else -> {
                    throw MessageException(MessagesModel(R.string.msg_internal_exception))
                }
            }
        } catch (ex: Exception) {
            saveSaleLog(saleLog, goods)
            throw ex
        }
    }

    suspend fun sendBeerSaleLog(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        beers: List<CombinationBeersInfo>
    ): String {
        val request = saleLog.asSaleLogRequest()

        request.saleLogDetails =
            beers.asSaleLogDetailEntityBeer(0L, saleLog).asSaleLogDetailsRequestModelBeer()
        try {
            val result = withContext(Dispatchers.IO) {
                apiService.createSaleLog(AuthRepository.getAccessToken(), request)
            }

            when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    return response.body.id
                }
                is ApiErrorResponse -> {
                    throw MessageException(MessagesModel(response.body.message.toString()))
                }
                else -> {
                    throw MessageException(MessagesModel(R.string.msg_internal_exception))
                }
            }
        } catch (ex: Exception) {
            saveSaleLog(saleLog, beers.first())
            throw ex
        }
    }


    suspend fun sendBeerSaleLogMaintain(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        beers: List<CombinationBeersInfo>
    ): String {
        val request = saleLog.asSaleLogRequest()

        request.saleLogDetails =
            beers.asSaleLogDetailEntityBeerMaintain(0L, saleLog).asSaleLogDetailsRequestModelBeer()
        try {
            val result = withContext(Dispatchers.IO) {
                apiService.createSaleLog(AuthRepository.getAccessToken(), request)
            }

            when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    return response.body.id
                }
                is ApiErrorResponse -> {
                    throw MessageException(MessagesModel(response.body.message.toString()))
                }
                else -> {
                    throw MessageException(MessagesModel(R.string.msg_internal_exception))
                }
            }
        } catch (ex: Exception) {
            saveSaleLog(saleLog, beers.first())
            throw ex
        }
    }

    suspend fun insert(saleLog: com.nereus.craftbeer.database.entity.SaleLog): Long {
        return saleLogDao.insert(saleLog)
    }
}
