package com.nereus.craftbeer.repository

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.DEFAULT_API_LIMIT
import com.nereus.craftbeer.constant.DEFAULT_API_PAGE
import com.nereus.craftbeer.database.dao.TopUpDao
import com.nereus.craftbeer.database.entity.asTopUpRequest
import com.nereus.craftbeer.database.entity.asUnsyncLogs
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.networking.ApiErrorResponse
import com.nereus.craftbeer.networking.ApiResponse
import com.nereus.craftbeer.networking.ApiSuccessResponse
import com.nereus.craftbeer.networking.CoreApiWithoutLiveData
import com.nereus.craftbeer.util.toISOString
import com.nereus.craftbeer.util.toBaseDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Top up repository
 *
 * @property apiService
 * @property topUpDao
 * コンストラクタ  Top up repository
 */
class TopUpRepository
@Inject constructor(
    private val apiService: CoreApiWithoutLiveData,
    private val topUpDao: TopUpDao
) {
    /**
     * Save top up
     *
     * @param topUp
     */
    private suspend fun saveTopUp(
        topUp: com.nereus.craftbeer.database.entity.TopUp
    ) {
        try {
            val topUpId = withContext(Dispatchers.IO) {
                topUpDao.insert(topUp)
            }

            Timber.i("-------- topUpId inserted: %d", topUpId)

        } catch (ex: Exception) {
            Timber.e(ex, "Failed to save TopUp to DB...")
            throw MessageException(MessagesModel(R.string.msg_internal_exception))
        }
    }

    /**
     * Get unsync top ups
     *
     * @return
     */
    suspend fun getUnsyncTopUps(): List<UnsyncLog> {
        Timber.d("---------------- getUnsyncSaleLogs")
        return try {
            withContext(Dispatchers.IO) {
                topUpDao.getAll()
            }.asUnsyncLogs()
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_load_sale_log_failed)
            )
        }
    }

    /**
     * Get many
     *
     * @param pointPlusId
     * @param startTime
     * @param endTime
     * @param page
     * @param limit
     * @return
     */
    suspend fun getMany(
        pointPlusId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        page: Int = DEFAULT_API_PAGE,
        limit: Int = DEFAULT_API_LIMIT
    ): List<TopUp> {
        Timber.d("---------------- getSaleLogsByPointPlusId")
        return try {
            val saleLogs = withContext(Dispatchers.IO) {
                apiService.getTopUps(
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

    /**
     * Send top ups
     *
     */
    suspend fun sendTopUps() {
        try {
            val topUps = withContext(Dispatchers.IO) {
                getUnsyncTopUps()
            }.asTopUpRequests()
            sendTopUps(topUps)
        } catch (ex: Exception) {
            throw MessageException(
                cause = ex,
                messagesModel = MessagesModel(R.string.msg_sync_sale_log_failed)
            )
        }
    }

    /**
     * Send top ups
     *
     * @param topUps
     */
    suspend fun sendTopUps(topUps: List<TopUpCreateRequest>) {

        if (topUps.isEmpty()) {
            return
        }

        val messagesModel = MessagesModel()

        topUps.forEach {
            val result = withContext(Dispatchers.IO) {
                apiService.createTopUp(AuthRepository.getAccessToken(), it)
            }

            when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    it.topUp?.let { it1 -> topUpDao.delete(it1) }
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

    /**
     * Send top up
     *
     * @param topUp
     * @return
     */
    suspend fun sendTopUp(topUp: com.nereus.craftbeer.database.entity.TopUp): String {
        try {
            val result = withContext(Dispatchers.IO) {
                apiService.createTopUp(AuthRepository.getAccessToken(), topUp.asTopUpRequest())
            }

            return when (val response = ApiResponse.create(result)) {
                is ApiSuccessResponse -> {
                    response.body.id
                }
                is ApiErrorResponse -> {
                    throw MessageException(MessagesModel(response.body.message.toString()))
                }
                else -> {
                    throw MessageException(MessagesModel(R.string.msg_internal_exception))
                }
            }
        } catch (ex: MessageException) {
            throw ex
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to send TopUp. Save to DB...")
            saveTopUp(topUp)
            throw MessageException(MessagesModel(R.string.msg_internal_exception))
        }
    }

    /**
     * Insert
     *
     * @param topUp
     * @return
     */
    suspend fun insert(topUp: com.nereus.craftbeer.database.entity.TopUp): Long {
        return topUpDao.insert(topUp)
    }

    /**
     * Set topups printed
     *
     * @param topUpId
     */
    suspend fun setTopupsPrinted(
        topUpId: String
    ) {
        try {
            withContext(Dispatchers.IO) {
                apiService.setPrintedTopup(
                    topUpId,
                    AuthRepository.getAccessToken()
                )
            }

        } catch (ex: Exception) {
            Timber.e(ex, "Failed to set TopUp printed")
        }
    }

    /**
     * Set topups issued
     *
     * @param topUpId
     */
    suspend fun setTopupsIssued(
        topUpId: String
    ) {
        try {
            withContext(Dispatchers.IO) {
                apiService.setIssuedTopup(
                    topUpId,
                    AuthRepository.getAccessToken()
                )
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to set TopUp Issued")
        }
    }
}
