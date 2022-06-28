package com.nereus.craftbeer.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.PREF_DEVICE_CODE
import com.nereus.craftbeer.constant.PREF_DEVICE_FILE
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.obniz.ObnizInfo
import com.nereus.craftbeer.repository.ErrorLogRepository
import com.nereus.craftbeer.repository.GoodsRepository
import com.nereus.craftbeer.repository.SaleLogRepository
import com.nereus.craftbeer.repository.TopUpRepository
import com.nereus.craftbeer.socket.SocketIO
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.toBaseDateTime
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.Callable

/**
 * Setting master view model
 *
 * @property savedStateHandle
 * @property goodsRepository
 * @property saleLogRepository
 * @property topUpRepository
 * @property _errorLogRepository
 * @property mSocket
 * @constructor
 *
 * @param application
 */
class SettingMasterViewModel @ViewModelInject constructor(
    @Assisted var savedStateHandle: SavedStateHandle,
    application: Application,
    private val goodsRepository: GoodsRepository,
    private val saleLogRepository: SaleLogRepository,
    private val topUpRepository: TopUpRepository,
    override var _errorLogRepository: ErrorLogRepository,
    var mSocket: Socket
) :
    BaseViewModel(application) {
    private val _beerPouring = MutableLiveData<BeerPouring>()
    private val _unsyncSaleLogs = MutableLiveData<List<UnsyncLog>>()
    private val _beers = MutableLiveData<List<CombinationBeersInfo>>()
    private val _obnizServers = MutableLiveData<List<ObnizInfo>>()
    private val _tabBeerShop = MutableLiveData<List<CombinationBeersInfo>>()
    private val _tabBeerDevice = MutableLiveData<List<TabBeerDeviceInfoList>>()
    private var tabletId: String = ""


    val unsyncSaleLogs: LiveData<List<UnsyncLog>>
        get() = _unsyncSaleLogs

    val beers: LiveData<List<CombinationBeersInfo>>
        get() = _beers

    val obnizs: LiveData<List<ObnizInfo>>
        get() = _obnizServers

    val tapBeerShop: LiveData<List<CombinationBeersInfo>>
        get() = _tabBeerShop

    val tapBeerDevice: LiveData<List<TabBeerDeviceInfoList>>
        get() = _tabBeerDevice

    val beerPouring: LiveData<BeerPouring>
        get() = this._beerPouring

    init {
        val sharedPreference =
            application.getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        tabletId = sharedPreference.getString(PREF_DEVICE_CODE, EMPTY_STRING).toString()

        _beerPouring.value = BeerPouring()
    }

    /**
     * 販売ログを読み込む
     *
     */
    fun loadSaleLogs() {
        viewModelScope.launch() {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                _unsyncSaleLogs.postValue(saleLogRepository.getUnsyncSaleLogs())

                val fromSaleLogs = withContext(Dispatchers.IO) {
                    saleLogRepository.getUnsyncSaleLogs()
                }
                val fromTopUps = withContext(Dispatchers.IO) {
                    topUpRepository.getUnsyncTopUps()
                }
                _unsyncSaleLogs.postValue(fromSaleLogs + fromTopUps)


            } catch (ex: MessageException) {
                ex.setCallback(Callable { loadSaleLogs() })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }

    /**
     * ビールをロード
     *
     */
    fun loadBeers() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                val beers = withContext(Dispatchers.IO) {
                    goodsRepository.getTabBeersServerList()
                }
                val obnizs = withContext(Dispatchers.IO) {
                    goodsRepository.getObnizServerList()
                }
//                val tabBeer = withContext(Dispatchers.IO) {
//                    goodsRepository.getTapBeerShopList()
//                }
                val tabBeerDev = withContext(Dispatchers.IO) {
                    goodsRepository.getTapBeerDevice()
                }
                _beers.postValue(beers)
                _obnizServers.postValue(obnizs)
                _tabBeerDevice.postValue(tabBeerDev)
//                _tabBeerShop.postValue((tabBeer))
                _beers.postValue(goodsRepository.getTabBeersServerList())

            } catch (ex: MessageException) {
                ex.setCallback(Callable { loadBeers() })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }

    /**
     * ビールのステータスを更新する
     *
     * @param beersInfo
     */
    fun updateBeersStatus(beersInfo: CombinationBeersInfo) {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                goodsRepository.updateBeersStatus(beersInfo)
                setSuccessMessage(R.string.msg_update_successfully)
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }

    /**
     * 販売ログを送信する
     *
     */
    fun sendSaleLogs() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                saleLogRepository.sendSaleLogs(buildSaleLogRequests())
                topUpRepository.sendTopUps(buildTopUpRequests())
            } catch (ex: MessageException) {
                ex.setCallback(Callable { sendSaleLogs() })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }

    /**
     * Build sale log requests
     *
     * @return
     */
    private fun buildSaleLogRequests(): List<SaleLogCreateRequest> {
        return _unsyncSaleLogs.value?.asSaleLogRequests() ?: ArrayList()
    }

    /**
     * ビールサーバーを更新する
     *
     * @param updateTapBeerId
     * @param updateRequest
     */
    fun updateTapBeerServer(updateTapBeerId: String, updateRequest: UpdateTapBeerServerRequest) {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                goodsRepository.updateTapBeerServer(updateTapBeerId, updateRequest)
                setSuccessMessage(R.string.msg_update_successfully)
            } catch (ex: MessageException) {
                ex.setCallback(Callable { updateTapBeerServer(updateTapBeerId, updateRequest) })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
                loadBeers()
            }
        }
    }

    /**
     * Build top up requests
     *
     * @return
     */
    private fun buildTopUpRequests(): List<TopUpCreateRequest> {
        return _unsyncSaleLogs.value?.asTopUpRequests() ?: ArrayList()

    }

    /**
     * Connect socket
     *
     */
    fun connectSocket() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                withContext(Dispatchers.IO) {
                    mSocket = SocketIO.getSocket()!!
                    mSocket.connect()
                }
            } catch (ex: Exception) {
                setException(
                    MessageException(
                        MessagesModel(
                            ErrorLogCode.ES004,
                            coreMsgArgs = listOf(tabletId)
                        )
                    )
                )
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }


    /**
     * Pour beer
     */
    private val pourBeer =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                var amount: Int
                try {
                    displayToast(application.applicationContext, "ビールが注がれています...")
                    amount = data.getInt("beerAmount")
                    pourBeer(amount)
                    delay(200)
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }

    /**
     * Pour beer
     *
     * @param amount
     */
    fun pourBeer(amount: Int) {
        _beerPouring.value?.let {
            val beerPouring = BeerPouring(
                amountInMl = it.amountInMl + amount,
                total = 0.0,
                balance = 0,
                tax = 0.0,
                sellingPrice = 0.0
            )
            _beerPouring.value = beerPouring
        }
    }


    /**
     * Approve open
     */
    val approveOpen =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                var message: String
                try {
                    message = data.getString("message")
                    displayToast(application.applicationContext, message, Toast.LENGTH_LONG)
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }


    /**
     * Initial event socket
     *
     */
    fun initialEventSocket() {
        mSocket.on("pourBeerNow", pourBeer)
        mSocket.on("approveOpen", approveOpen)
    }

    /**
     * Emit open beer
     *
     * @param tabBeerId
     */
    fun emitOpenBeer(tabBeerId: String) {
        val maximumBeerAmount = -1
        val jsonObject = JSONObject()
        jsonObject.put("tabBeerId", tabBeerId)
        jsonObject.put("maximumBeerAmount", maximumBeerAmount)
        mSocket.emit("openBeer", jsonObject)
        setException(
            MessageException(
                MessagesModel(
                    obnizId = tabBeerId,
                    eventType = "Maintenance",
                    messageLogCode = MessageLogCode.EB015,
                    coreMsgArgs = listOf(tabBeerId, maximumBeerAmount)
                )
            )
        )
    }

    /**
     * Setup pouring beer
     *
     * @param tabBeerId
     */
    fun setupPouringBeer(tabBeerId: String) {
        _beerPouring.value = BeerPouring(
            balance = 0,
            sellingPrice = 0.0
        )
        initialEventSocket()
        emitOpenBeer(tabBeerId)
    }

    /**
     * Force stop socket
     *
     * @param obnizId
     */
    fun forceStopSocket(obnizId: String) {
        Timber.i("forceStopSocket: obnizId: " + obnizId)
        mSocket.emit("forceStop", obnizId)
        setException(
            MessageException(
                MessagesModel(
                    obnizId = obnizId,
                    eventType = "Maintenance",
                    messageLogCode = MessageLogCode.EB014,
                    coreMsgArgs = listOf(obnizId)
                )
            )
        )
    }


    /**
     * Simulate pour out gross beer
     *
     */
    fun simulatePourOutGrossBeer() {
        pourBeer(300)
    }

    /**
     * Handle pour beer gross
     *
     * @param beer
     */
    fun handlePourBeerGross(beer: CombinationBeersInfo) {
        if (_beerPouring.value!!.amountInMl != 0) {
            viewModelScope.launch {
                try {
                    val saleLog = buildSaleLog()
                    val saleLogId = withContext(Dispatchers.IO) {
                        saleLogRepository.sendBeerSaleLogMaintain(saleLog, listOf(beer))
                    }
                } catch (ex: MessageException) {
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                }
            }
        }
    }

    /**
     * Build sale log
     *
     * @return
     */
    private fun buildSaleLog(): com.nereus.craftbeer.database.entity.SaleLog {

        return com.nereus.craftbeer.database.entity.SaleLog(
            pointPlusId = EMPTY_STRING,
            balanceAfter = 0,
            balanceBefore = 0,
            saleLogName = _beers.value!!.buildSaleLogBeerName(),
            paymentMethod = 1,
            totalSellingPrice = 0.0,
            totalAmount = beerPouring!!.value?.amountInMl!!,
            tax = 0.0,
            productType = ProducType.GOODS.getValue(),
            createdAt = LocalDateTime.now().toBaseDateTime(),
            takeAway = 0,
            receiptCode = EMPTY_STRING,
            companyId = Company.fromPreferences().id!!,
            shopId = ShopInfo.fromPreferences().id!!
        )
    }
}
