package com.nereus.craftbeer.repository

import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.constant.POINT_PLUS_NO_ERROR_CODE
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.model.pointplus.v4.transaction.BasedCardResponseTransaction
import com.nereus.craftbeer.model.pointplus.v4.transaction.CommonTransaction
import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardRequestTransactions
import com.nereus.craftbeer.networking.*
import com.nereus.craftbeer.util.TLogger
import com.nereus.craftbeer.util.toBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.jvm.Throws

/**
 * Point plus repository
 *
 * @property pointPlusService
 * @property coreService
 * コンストラクタ  Point plus repository
 */
class PointPlusRepository
@Inject constructor(
    private val pointPlusService: PointPlusApiWithoutLiveData,
    private val coreService: CoreApiWithoutLiveData
) {

    /**
     * Call card api
     *
     * @param transactions
     * @return
     */
    @Throws(MessageException::class)
    suspend fun callCardApi(transactions: BasedCardRequestTransactions): BasedCardResponseTransaction {
        try {
            TLogger.writeln(this.javaClass.name + "::callCardApi() START")
            /*Check card status*/ //TODO
            checkCardStatus(transactions)

            fillClientSignature(transactions.transaction)
            Timber.e("------ call XML aPI queryBalance")
            val balance = withContext(Dispatchers.IO) {
                pointPlusService.callCardApi(transactions, provideCompanyKey())
            }
            when (val response = ApiResponse.create(balance)) {
                is ApiSuccessResponse -> {
                    if (response.body.transaction.errorCode.equals(POINT_PLUS_NO_ERROR_CODE)) {
                        Timber.i("" + response.body.transaction.errorCode.isNullOrBlank())
                        return response.body.transaction
                    } else {
                        // Check error code of poinplus and throw message exception
                        Timber.e(response.body.transaction.message1!!)
                        Timber.e(response.body.transaction.message2!!)
                        TLogger.writeln("respnse---")
                        TLogger.writeln(response.toString())
                        TLogger.writeln(response.body.toString())
                        TLogger.writeln(response.body.transaction.toString())
                        TLogger.writeln("response.body.transaction.message1:" + response.body.transaction.message1!!)
                        TLogger.writeln("response.body.transaction.message2:" + response.body.transaction.message2!!)
                        TLogger.writeln("respnse---")

                        throw MessageException(
                            MessagesModel(
                                errorLogCode = ErrorLogCode.ES007,
                                coreMsgArgs = listOf(response.body.transaction.errorCode!!)
                            )
                        )
                    }
                }
                is ApiErrorResponse -> {
                    Timber.e(response.body.message.toString())
                    throw MessageException(
                        MessagesModel(
                            ErrorLogCode.ES007
                        )
                    )
                }
                else -> {
                    throw MessageException(
                        MessagesModel(
                            ErrorLogCode.ES007
                        )
                    )
                }
            }
        } catch (ex: MessageException) {
            throw ex
        } catch (ex: Exception) {
            Timber.e(ex)
            throw MessageException(
                MessagesModel(
                    ErrorLogCode.ES007
                )
            )
        }finally {
            TLogger.writeln(this.javaClass.name + "::callCardApi() END")
        }
    }

    /**
     * Check card status
     *
     * @param transactions
     */
    private suspend fun checkCardStatus(transactions: BasedCardRequestTransactions) {
        val cardStatus = withContext(Dispatchers.IO) {
            coreService.getLockCardStatus(
                transactions.transaction.memberCode!!,
                AuthRepository.getAccessToken()
            )
        }
        when (val response = ApiResponse.create(cardStatus)) {
            is ApiSuccessResponse -> {
                TLogger.writeln(response.body.toString())
                TLogger.writeln("AuthRepository.getDeviceId()" + AuthRepository.getDeviceId())

                Timber.i("-------------------- %s", response.body.toString())
                Timber.i(
                    "--------------------AuthRepository.getDeviceId() %s",
                    AuthRepository.getDeviceId()
                )
                TLogger.writeln("response.body.lastUpdateDeviceId" + response.body.lastUpdateDeviceId)
                Timber.i(
                    "--------------------response.body.lastUpdateDeviceId %s",
                    response.body.lastUpdateDeviceId
                )
                TLogger.writeln("response.body.lastUpdateDeviceId.equals(AuthRepository.getDeviceId())" + AuthRepository.getDeviceId().equals(response.body.lastUpdateDeviceId))
                Timber.i(
                    "--------------------response.body.lastUpdateDeviceId.equals(AuthRepository.getDeviceId()) %s",
                    AuthRepository.getDeviceId().equals(response.body.lastUpdateDeviceId)
                )
                TLogger.writeln("check card response.body.isLocked: " + response.body.isLocked.toBoolean().toString())
                Timber.i(
                    "--- check card response.body.isLocked: %s",
                    response.body.isLocked.toBoolean().toString()
                )

                if (response.body.isLocked.toBoolean()) {
                    TLogger.writeln("check card response.body.isLocked 1" )
                    if (!AuthRepository.getDeviceId().equals(response.body.lastUpdateDeviceId)) {
//                    if (!response.body.lastUpdateDeviceId.equals("2")) {
                        /* Card is in transaction by another device*/
                        throw MessageException(
                            MessagesModel(
                                ErrorLogCode.EB002
                            )
                        )
                    }
                } else {
                    /* Card is unlocked, perform lock card*/
                    TLogger.writeln("check card response.body.isLocked 2" )
                    transactions.transaction.memberCode?.let { lockCard(it) }
                }
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
            else -> {
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
        }
    }

    /**
     * Lock card
     *
     * @param memberCode
     */
    suspend fun lockCard(memberCode: String) {
        TLogger.writeln("lockCard() START")
        Timber.i("--- locking card: %s", memberCode)
        TLogger.writeln("locking card:%s".format(memberCode) )

        val lockCard = withContext(Dispatchers.IO) {
            coreService.lockCard(
                memberCode,
                AuthRepository.getAccessToken()
            )
        }
        TLogger.writeln("lockCard() response:%s".format(memberCode) )
        when (val response = ApiResponse.create(lockCard)) {
            is ApiSuccessResponse -> {
                TLogger.writeln("lockCard() ApiSuccessResponse:%s".format(response.body.toString()))
            }
            is ApiErrorResponse -> {
                TLogger.writeln("lockCard() ApiErrorResponse:%s".format(response.body.message.toString()))
                Timber.e(response.body.message.toString())
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
            else -> {
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
        }
        TLogger.writeln("lockCard() END")
    }

    /**
     * Unlock card
     *
     * @param memberCode
     */
    suspend fun unlockCard(memberCode: String) {
        Timber.i("--- unlocking card: %s", memberCode)
        val lockCard = withContext(Dispatchers.IO) {
            coreService.unlockCard(
                memberCode,
                AuthRepository.getAccessToken()
            )
        }
        when (val response = ApiResponse.create(lockCard)) {
            is ApiSuccessResponse -> {
                Timber.i(
                    "--- unlocking card successfully: %s, response.body.isLocked: %s",
                    memberCode,
                    response.body.isLocked.toString()
                )
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
            else -> {
                throw MessageException(
                    MessagesModel(
                        ErrorLogCode.ES007
                    )
                )
            }
        }
    }

    /**
     * Fill client signature
     *
     * @param request
     */
    private fun fillClientSignature(request: CommonTransaction) {
        /*A key to identify the client within point + plus.
        Issued by Pointplus for each client.*/
        request.clientSignature = BuildConfig.POINT_PLUS_CLIENT_SIGNATURE
    }

    /**
     * Provide company key
     *
     * @return
     */
    private fun provideCompanyKey(): String {
        return BuildConfig.POINT_PLUS_COMPANY_KEY
    }
}