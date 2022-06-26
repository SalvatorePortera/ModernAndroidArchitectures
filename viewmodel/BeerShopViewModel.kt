package com.nereus.craftbeer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.google.firebase.crashlytics.internal.Logger
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.cardTerminal
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.payment.*
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.repository.*
import com.nereus.craftbeer.socket.SocketIO
import com.nereus.craftbeer.util.*
import com.nereus.craftbeer.util.livedata.Event
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Runnable
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Callable
import javax.smartcardio.Card
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.roundToInt


class BeerShopViewModel @ViewModelInject constructor(
    @Assisted var savedStateHandle: SavedStateHandle,
    application: Application,
    private val goodsRepository: GoodsRepository,
    private val saleLogRepository: SaleLogRepository,
    val shopRepository: ShopRepository,
    val pointPlusRepository: PointPlusRepository,
    override var _errorLogRepository: ErrorLogRepository,
    var mSocket: Socket
) :
    BaseViewModel(application) {
    private var tabletId: String = ""
    private val _beers = MutableLiveData<List<CombinationBeersInfo>>()
    private val _beersNoFilter = MutableLiveData<List<CombinationBeersInfo>>()
    private val _selectedBeer = MutableLiveData<CombinationBeersInfo>()
    private val _beerPouring = MutableLiveData<BeerPouring>()
    private val _balance = MutableLiveData<Event<Int>>()
    private val _payment = MutableLiveData<Event<Payment>>()
    private val _paymentStrategy = MutableLiveData<PaymentStrategy>()
    private val _beerShopFlowHandler = MutableLiveData<Event<BeerShopFlowHandler>>()
    private val _lockHandler = MutableLiveData<Event<LockHandler>>()
    private val paymentTimerHandler = Handler()
    private val _beerPercentag = MutableLiveData<Int>()
    private val _beerPos = MutableLiveData<Int>()

    init {
        _paymentStrategy.value = PointPlusPaymentStrategy()
        _beerShopFlowHandler.value = Event(BeerShopFlowHandler())
        _lockHandler.value = Event(LockHandler())
        _beerPercentag.value = 0
        _beerPos.value = 0
        val sharedPreference =
            application.getSharedPreferences(PREF_DEVICE_FILE, Context.MODE_PRIVATE)
        tabletId = sharedPreference.getString(PREF_DEVICE_CODE, EMPTY_STRING).toString()
    }

    val beerPercentage: LiveData<Int>
        get() = this._beerPercentag

    val beerPos: LiveData<Int>
        get() = this._beerPos

    val beers: LiveData<List<CombinationBeersInfo>>
        get() = this._beers

    val beersNoFilter: LiveData<List<CombinationBeersInfo>>
        get() = this._beersNoFilter

    val selected_beer: LiveData<CombinationBeersInfo>
        get() = this._selectedBeer

    val beerPouring: LiveData<BeerPouring>
        get() = this._beerPouring

    val payment: LiveData<Event<Payment>>
        get() = this._payment

    val balance: LiveData<Event<Int>>
        get() = this._balance

    val beerShopFlowHandler: LiveData<Event<BeerShopFlowHandler>>
        get() = this._beerShopFlowHandler

    val lockHandler: LiveData<Event<LockHandler>>
        get() = this._lockHandler


    fun setSelectedBeer(beersInfo: CombinationBeersInfo) {
        TLogger.writeln(this.javaClass.name + "::setSelectedBeer() START")
        _selectedBeer.value = beersInfo
        _beerPercentag.value =
            (((beersInfo.remainingAmount!!.toDouble()) / (beersInfo.originalAmount!!.toDouble())) * 100).roundToInt()
        var countPos = 1
        for (item in _beersNoFilter.value!!) {
            if (selected_beer.value?.tapBeerId?.equals(item.tapBeerId)!!) {
                setBeerPos(countPos)
                break
            }
            countPos += 1
        }
        TLogger.writeln(this.javaClass.name + "::setSelectedBeer() END")
    }

    fun addCorrectionAmount(): Int {
        TLogger.writeln(this.javaClass.name + "::addCorrectionAmount() START")
        if (!_beerShopFlowHandler.value!!.peekContent().isBeerPouringCorrectionDone) {
            Timber.d("correction amount is added")
            _beerShopFlowHandler.value!!.peekContent().isBeerPouringCorrectionDone = true
            return ShopInfo.fromPreferences().beerPouringErrorCorrectionValue
        }
        TLogger.writeln(this.javaClass.name + "::addCorrectionAmount() END")
        return 0
    }

    @Synchronized
    fun pourBeer(amount: Int) {
        TLogger.writeln(this.javaClass.name + "::pourBeer() START")
        TLogger.writeln("amount:%d".format(amount))
        //Timber.i("Pouring ViewModel:: %d", amount)
        _beerPouring.value?.let {
            removePaymentTimer()
            val total = (it.amountInMl + amount) * it.sellingPrice
            val tax = total * _selectedBeer.value!!.taxRate

            var newBalance = floor((_balance.value!!.peekContent() - total - tax)).toInt()
            if (newBalance < 0) {
                newBalance = 0
            }

            val beerPouring = BeerPouring(
                amountInMl = it.amountInMl + amount,
                total = total + tax,
                balance = newBalance,
                tax = tax,
                sellingPrice = it.sellingPrice
            )
            _beerPouring.value = beerPouring
            TLogger.writeln("beerPouring:" + beerPouring.toString())
            //Timber.i("beerPouring ViewModel:: %s", beerPouring.toString())

            // TODO check min amount , 1ml?
            TLogger.writeln("beerPouring.balance:" + beerPouring.balance.toString())
            TLogger.writeln("it.sellingPrice:" + it.sellingPrice.toString())
            TLogger.writeln("it.amountInMl:" + it.amountInMl.toString())
            TLogger.writeln("_selectedBeer.value!!.taxRate:" + _selectedBeer.value!!.taxRate.toString())

            //Timber.i("beerPouring ViewModel beerPouring.balance: %s", beerPouring.balance.toString())
            //Timber.i("beerPouring ViewModel it.sellingPrice: %s", it.sellingPrice.toString())
            //Timber.i("beerPouring ViewModel it.amountInMl: %s", it.amountInMl.toString())

            var rval = it.sellingPrice * it.amountInMl * (1 + _selectedBeer.value!!.taxRate)
            TLogger.writeln("eq right:" + rval.toString())

            /**
             * この計算が返すはずのないタイミングでTrueを返しているのが問題
             */
            /*
                Timber.i("Pouring Balance: %s", balance.value!!.peekContent().toString())
                val str = balance.value!!.peekContent().toString()
                Timber.i("Pouring Balance str: %s", str)
                val str2 = _balance.value!!.peekContent().toString()
                Timber.i("Pouring Balance str2: %s", str2)
                Timber.i("beerPouring.balance: %s, eq right: %s", beerPouring.balance.toString(), rval.toString())

             */

            val origBalance = balance.value!!.peekContent()


            // 残高と抽出時の金額を比較し、超えていれば現在の残高をMAXとして送る。最終的に送るデータは税込のため、beerPouring.taxは一旦考えない。
            if (beerPouring.total > origBalance) {
                beerPouring.total = origBalance.toDouble()
            }

            if (origBalance < it.sellingPrice * it.amountInMl * (1 + _selectedBeer.value!!.taxRate)) {
                TLogger.writeln("beerPouring:1")
                Timber.i("beerPouring:1")
                setException(
                    MessageException(
                        MessagesModel(
                            messageLogCode = MessageLogCode.EB017,
                            eventType = "pourBeer",
                            obnizId = _selectedBeer.value!!.obnizId.toString(),
                            coreMsgArgs = listOf(
                                beerPouring.balance.toString(),
                                (it.sellingPrice * it.amountInMl * (1 + _selectedBeer.value!!.taxRate)).toString()
                            )
                        )
                    )
                )
                TLogger.writeln("beerPouring:2")
                //Timber.i("beerPouring:2")
                paymentAction.run()
                TLogger.writeln("beerPouring:3")
                //Timber.i("beerPouring:3")
            } else {
                //Timber.i("beerPouring set PaymentTimer 1")
                setPaymentTimer()
            }
        }
        TLogger.writeln(this.javaClass.name + "::pourBeer() END")
    }

    fun setBeerPercentage(percentage: Int) {
        TLogger.writeln(this.javaClass.name + "::setBeerPercentage() START")
        _beerPercentag.value = percentage
        TLogger.writeln(this.javaClass.name + "::setBeerPercentage() END")
    }

    fun setBeerPos(pos: Int) {
        TLogger.writeln(this.javaClass.name + "::setBeerPos() START")
        _beerPos.value = pos
        TLogger.writeln(this.javaClass.name + "::setBeerPos() END")
    }


    fun setPayment(payment: Payment) {
        TLogger.writeln(this.javaClass.name + "::setPayment() START")
        _payment.value = Event(payment)
        TLogger.writeln(this.javaClass.name + "::setPayment() END")
    }

    fun getBalanceString(balance: Int): String {
        TLogger.writeln(this.javaClass.name + "::getBalanceString() START")
        TLogger.writeln(this.javaClass.name + "::getBalanceString() END")
        return balance.toThousandSeparatorStringWithoutPrefix()
    }

    fun getAmountInMlString(amountInMl: Number): String {
        TLogger.writeln(this.javaClass.name + "::getAmountInMlString() START")
        TLogger.writeln(this.javaClass.name + "::getAmountInMlString() END")
        return DecimalFormat(ML_THOUSAND_DECIMAL_FORMAT).format(amountInMl)
    }

    fun getTotalString(total: Number): String {
        TLogger.writeln(this.javaClass.name + "::getTotalString() START")
        TLogger.writeln(this.javaClass.name + "::getTotalString() START")
        return DecimalFormat(THOUSAND_DECIMAL_FORMAT).format(total)
    }

    fun loadBeers() {
        viewModelScope.launch {
            TLogger.writeln(this.javaClass.name + "::loadBeers() START")
            if (_beers.value?.isEmpty() == true) {
                setLoadingState(CommonConst.LOADING_VISIBLE)
            }
            try {
                _beers.postValue(goodsRepository.getTabBeersServerList())

                _beersNoFilter.postValue(goodsRepository.getTabBeersServerList())

                _beers.postValue(goodsRepository.getTabBeersServerList())

            } catch (ex: MessageException) {
                ex.setCallback(Callable { loadBeers() })
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                setLoadingState(CommonConst.LOADING_GONE)
            }
            TLogger.writeln(this.javaClass.name + "::loadBeers() END")
        }
    }


    private suspend fun getShopInfo(receipt: Receipt? = null): ShopInfo {
        TLogger.writeln(this.javaClass.name + "::getShopInfo() START")
        return if (receipt == null || receipt.shopId.isBlank()) {
            ShopInfo.fromPreferences()
        } else {
            shopRepository.getShop(shopId = receipt.shopId)
        }
    }

    private suspend fun getCompanyInfo(receipt: Receipt? = null): Company {
        TLogger.writeln(this.javaClass.name + "::getCompanyInfo() START")
        return if (receipt == null || receipt.companyId.isBlank()) {
            Company.fromPreferences()
        } else {
            shopRepository.getCompany(companyId = receipt.companyId)
        }
    }

    fun pay() {
        viewModelScope.launch {
            TLogger.writeln(this.javaClass.name + "::pay() START")
            setLoadingState(CommonConst.LOADING_VISIBLE)
            _payment.value?.let {
                it.peekContent().total = floor(_beerPouring.value!!.total).toInt()
                val company = withContext(Dispatchers.IO) {
                    getCompanyInfo()
                }
                val shop = withContext(Dispatchers.IO) {
                    getShopInfo()
                }
                try {
                    _payment.value!!.peekContent().apply {
                        receiptCode = genReceiptCode(company.companyCode, shop.shopCode)
                        receiptTimestamp = LocalDateTime.now().toBaseDateTime()
                    }
                    // Store balance to payment
                    val result = _paymentStrategy.value!!.pay(it.peekContent())

                    _beerShopFlowHandler.postValue(
                        Event(
                            BeerShopFlowHandler(
                                isExecuted = true,
                                paymentResult = result
                            )
                        )
                    )
                } catch (ex: MessageException) {
                    ex.setCallback(Callable { pay() })
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    unlockCard(_payment.value!!.peekContent())
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
            TLogger.writeln(this.javaClass.name + "::pay() END")
        }
    }

    fun getChartData(): RadarData {
        TLogger.writeln(this.javaClass.name + "::getChartData() START")
        val entries1 = ArrayList<RadarEntry>()
        // Must multiply by 10 for rendering data to chart
        _selectedBeer.value?.apply {
            entries1.add(RadarEntry(smell * 10))
            entries1.add(RadarEntry(acidity * 10))
            entries1.add(RadarEntry(bitter * 10))
            entries1.add(RadarEntry(sweet * 10))
            entries1.add(RadarEntry(weight * 10))

        }

        val set1 = RadarDataSet(entries1, null)
        set1.apply {
            valueTextSize = 2f
            color = Color.parseColor("#868A8A")
            fillColor = Color.rgb(237, 175, 100)
            setDrawFilled(true)
            fillAlpha = 180
            lineWidth = 2f
            isDrawHighlightCircleEnabled = true
            setDrawHighlightIndicators(false)
        }

        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)
        val data = RadarData(sets)
        data.apply {
            setValueTextSize(12f)
            setDrawValues(false)
            setValueTextColor(Color.WHITE)
        }
        TLogger.writeln(this.javaClass.name + "::getChartData() END")
        return data
    }

    fun checkBalance() {
        viewModelScope.launch {
            TLogger.writeln(this.javaClass.name + "::checkBalance() START")
            setLoadingState(CommonConst.LOADING_VISIBLE)
            _payment.value?.let {
                try {
                    // Store balance to payment
                    val balance = _paymentStrategy.value?.getBalance(it.peekContent()) ?: 0
                    _balance.value = Event(balance)
                } catch (ex: Exception) {
                    restartHandler()
                    setException(ex)
                } finally {
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
            TLogger.writeln(this.javaClass.name + "::checkBalance() END")
        }
    }

    fun connectSocket() {
        TLogger.writeln(this.javaClass.name + "::connectSocket() START")
        Timber.i(this.javaClass.name + "::connectSocket() START")
        viewModelScope.launch {

            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                withContext(Dispatchers.IO) {
                    mSocket = SocketIO.getSocket()!!
                    mSocket.connect()
                    Timber.i("CONNECTED")
                }

            } catch (ex: Exception) {
                TLogger.writeln(this.javaClass.name + "::connectSocket() CONNECT_SOCKET_ERROR")
                Timber.i("CONNECT_SOCKET_ERROR")
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
        TLogger.writeln(this.javaClass.name + "::connectSocket() END")
    }

    private fun initialEventSocket() {
        mSocket.on("pourBeerNow", pourBeer)
        mSocket.on("approveOpen", approveOpen)
        mSocket.on("locked", locked)
        mSocket.on("error", error)
    }

    private fun emitOpenBeer() {
        TLogger.writeln(this.javaClass.name + "::emitOpenBeer() START")
        // 残高
        val balance = _balance.value!!.peekContent()
        // 1mlあたりの税込み売価
        val sellingPrice = _selectedBeer.value!!.sellingPrice * 1.1
        // ビールの残り量
        val remainingAmount = _selectedBeer.value!!.remainingAmount
        // 抽出可能量（金額ベース）
        // 金額で割った可能量では余り分で数円残るので1ml余分に抽出
        val canPourMl = floor((balance / sellingPrice)).toInt() + 1
        // 最大抽出量
        var maximumBeerAmount: Int = 0

        TLogger.writeln("balance:" + balance.toString())
        TLogger.writeln("sellingPrice:" + sellingPrice.toString())
        TLogger.writeln("remainingAmount:" + remainingAmount.toString())
        TLogger.writeln("canPourMl:" + canPourMl.toString())

        maximumBeerAmount = if (remainingAmount!! >= canPourMl) {
            canPourMl - addCorrectionAmount()
        } else {
            remainingAmount - addCorrectionAmount()
        }
        TLogger.writeln("maximumBeerAmount:" + maximumBeerAmount.toString())

        setException(
            MessageException(
                MessagesModel(
                    messageLogCode = MessageLogCode.EB016,
                    eventType = "pourBeer",
                    obnizId = _selectedBeer.value!!.obnizId.toString(),
                    coreMsgArgs = listOf(maximumBeerAmount.toString())
                )
            )
        )

        val jsonObject = JSONObject()
        jsonObject.put("tabBeerId", _selectedBeer.value!!.obnizId)
        jsonObject.put("maximumBeerAmount", maximumBeerAmount)
        mSocket.emit("openBeer", jsonObject)
        TLogger.writeln("emit openBeer:" + jsonObject.toString())
        TLogger.writeln(this.javaClass.name + "::emitOpenBeer() END")
    }

    private val pourBeer =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                TLogger.writeln("pourBeer recv...")
                TLogger.writeln("obnizId:%s\n%s".format(_selectedBeer.value!!.obnizId.toString(),data.toString()))

                // TODO : Log data from Obniz
                setException(
                    MessageException(
                        MessagesModel(
                            messageLogCode = MessageLogCode.EB013,
                            eventType = "pourBeer",
                            obnizId = _selectedBeer.value!!.obnizId.toString(),
                            coreMsgArgs = listOf(data.toString())
                        )
                    )
                )

                var amount: Int
                try {
                    amount = data.getInt("beerAmount")

                    val addCorrectionAmount = addCorrectionAmount()

                    amount += addCorrectionAmount

                    setException(
                        MessageException(
                            MessagesModel(
                                messageLogCode = MessageLogCode.EB011,
                                eventType = "pourBeer",
                                obnizId = _selectedBeer.value!!.obnizId.toString(),
                                coreMsgArgs = listOf(
                                    amount.toString(),
                                    addCorrectionAmount.toString()
                                )
                            )
                        )
                    )
                    displayToast(application.applicationContext, "ビールが注がれています${amount}ml...")

                    // Divide amount for better display
                    divideToPour(amount, mlPerTime = 2, delayInMs = 0)
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }

    /*  Divide amount to pour beer for better display */
    private suspend fun divideToPour(amountInMl: Int, mlPerTime: Int = 2, delayInMs: Long = 0) {
        TLogger.writeln(this.javaClass.name + "::divideToPour() START")

        repeat(amountInMl / mlPerTime) {
            delay(delayInMs)
            pourBeer(mlPerTime)
        }
        /* If amount is odd */
        val modulo = amountInMl % mlPerTime
        if (modulo != 0) {
            pourBeer(modulo)
        }
        TLogger.writeln(this.javaClass.name + "::divideToPour() END")
    }

    private val locked =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                TLogger.writeln("locked recv...")
                TLogger.writeln("obnizId:%s\n%s".format(_selectedBeer.value!!.obnizId.toString(),data.toString()))

                var message: String
                try {
                    message = data.getString("message")

                    _lockHandler.value = Event(
                        LockHandler(
                            isLock = true
                        )
                    )

                    setException(
                        MessageException(
                            MessagesModel(
                                messageLogCode = MessageLogCode.EB018,
                                eventType = "pourBeer",
                                obnizId = _selectedBeer.value!!.obnizId.toString(),
                                coreMsgArgs = listOf(message)
                            )
                        )
                    )
                    Toast.makeText(application.applicationContext, message, Toast.LENGTH_LONG)
                        .show()
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }

    val approveOpen =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                TLogger.writeln("approveOpen recv...")
                TLogger.writeln("obnizId:%s\n%s".format(_selectedBeer.value!!.obnizId.toString(),data.toString()))
                var message: String
                try {
                    message = data.getString("message")

                    // TODO : GET TIMESTAMP FOR DEBUG
                    val tsLong = System.currentTimeMillis() / 1000
                    val ts = tsLong.toString()
                    setException(
                        MessageException(
                            MessagesModel(
                                messageLogCode = MessageLogCode.EB010,
                                eventType = "pourBeer",
                                obnizId = _selectedBeer.value!!.obnizId.toString(),
                                coreMsgArgs = listOf(ts)
                            )
                        )
                    )
                    // TODO: END
                    Toast.makeText(application.applicationContext, message, Toast.LENGTH_LONG)
                        .show();
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }

    val error =
        Emitter.Listener { args ->
            viewModelScope.launch {
                val data = args[0] as JSONObject
                TLogger.writeln("error recv...")
                TLogger.writeln("obnizId:%s\n%s".format(_selectedBeer.value!!.obnizId.toString(),data.toString()))
                var message: String
                try {
                    message = data.getString("message")
                    Timber.i("ERROR LISTENER MESSAGE:$message")
                    if (message.equals(FLOWMETER_NOTFOUND)) {
                        setException(
                            MessageException(
                                MessagesModel(
                                    ErrorLogCode.ES010, coreMsgArgs = arrayListOf(tabletId)
                                )
                            )
                        )
                    } else if (message.contains(ALREADY_CONNECTED)) {
                        setException(
                            MessageException(
                                MessagesModel(
                                    ErrorLogCode.ES011, coreMsgArgs = arrayListOf(tabletId)
                                )
                            )
                        )
                    } else {
                        setException(
                            MessageException(
                                MessagesModel(
                                    ErrorLogCode.UNKNOWN,
                                    coreMsgArgs = arrayListOf(message, tabletId)
                                )
                            )
                        )
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
                }
            }
        }

    /**
     * 抽出中のRunnableだと思う
     */
    val paymentAction = Runnable {
        TLogger.writeln(this.javaClass.name + "::paymentAction() START")

        val amountInMl = _beerPouring.value!!.amountInMl
        if (amountInMl == 0) {
            TLogger.writeln("BeerShopViewModel:paymentAction amountInMl==0")
            //Timber.i("BeerShopViewModel:paymentAction amountInMl==0")
            //Timber.i("beerPouring set PaymentTimer 2")
            setPaymentTimer()
            return@Runnable
        }

        // update amount
        TLogger.writeln("BeerShopViewModel:paymentAction update amount")
        //Timber.i("BeerShopViewModel:paymentAction update amount")
        _selectedBeer.value!!.amountInMlL = amountInMl
        //TODO : DISPLAY FINAL VALUE ON SCREEN
        setException(
            MessageException(
                MessagesModel(
                    messageLogCode = MessageLogCode.EB012,
                    eventType = "pourBeer",
                    obnizId = _selectedBeer.value!!.obnizId.toString(),
                    coreMsgArgs = listOf(_selectedBeer.value!!.amountInMlL.toString())
                )
            )
        )
        // notify finish pouring
        TLogger.writeln("BeerShopViewModel:paymentAction notify finish pouring")
        Timber.i("BeerShopViewModel:paymentAction notify finish pouring")
        _beerShopFlowHandler.value = Event(
            BeerShopFlowHandler(
                isPouringFinished = true
            )
        )
        TLogger.writeln("BeerShopViewModel:paymentAction mSocket.close()")
        Timber.i("BeerShopViewModel:paymentAction mSocket.close()")
        mSocket.close()

        TLogger.writeln(this.javaClass.name + "::paymentAction() END")
    }

    private suspend fun sendSaleLog() {
        TLogger.writeln(this.javaClass.name + "::sendSaleLog() START")
        Timber.i("------ Save Sale Log --------")
        try {
            saleLogRepository.sendBeerSaleLog(buildSaleLog(), listOf(_selectedBeer.value!!))
        } catch (e: Exception) {
            Timber.e(e, "Failed to send SaleLog")
        }
        TLogger.writeln(this.javaClass.name + "::sendSaleLog() END")
    }

    fun closeSocket() {
        TLogger.writeln(this.javaClass.name + "::closeSocket() START")
        mSocket.close()
        TLogger.writeln(this.javaClass.name + "::closeSocket() END")
    }

    private fun buildSaleLog(): com.nereus.craftbeer.database.entity.SaleLog {
        TLogger.writeln(this.javaClass.name + "::buildSaleLog() START")
        var pointPlusId = _payment.value?.let {
            if (it.peekContent() is EMoneyPayment) {
                (it.peekContent() as EMoneyPayment).pointPlusId
            } else EMPTY_STRING
        } ?: EMPTY_STRING

        var slog = com.nereus.craftbeer.database.entity.SaleLog(
            pointPlusId = pointPlusId,
            balanceBefore = _beerShopFlowHandler.value!!.peekContent().paymentResult.balanceBefore,
            balanceAfter = _beerShopFlowHandler.value!!.peekContent().paymentResult.balanceAfter,
            saleLogName = selected_beer.value!!.beerName,
            paymentMethod = PaymentMethod.PAYMENT_HOUSE_MONEY.getValue(),
            totalSellingPrice = _beerPouring.value!!.total,
            totalAmount = _beerPouring.value!!.amountInMl,
            tax = _beerPouring.value!!.tax,
            productType = ProducType.TAP_BEER.getValue(),
            createdAt = LocalDateTime.now().toBaseDateTime(),
            takeAway = 0,
            receiptCode = _payment.value?.peekContent()?.receiptCode ?: EMPTY_STRING,
            companyId = Company.fromPreferences().id!!,
            shopId = ShopInfo.fromPreferences().id!!
        )
        TLogger.writeln("salse log:" + slog.toString())
        TLogger.writeln(this.javaClass.name + "::buildSaleLog() END")
        return slog
    }

    fun handlePostPayment() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    sendSaleLog()
                }
                cleanUp()
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            }
        }
    }


    fun setupPouringBeer() {
        TLogger.writeln(this.javaClass.name + "::setupPouringBeer() START")
        _beerPouring.value = BeerPouring(
            balance = _balance.value!!.peekContent(),
            sellingPrice = _selectedBeer.value!!.sellingPrice
        )
        initialEventSocket()
        emitOpenBeer()
        //Timber.i("beerPouring set PaymentTimer 3")
        setPaymentTimer()
        TLogger.writeln(this.javaClass.name + "::setupPouringBeer() END")
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun cleanUp() {
        _balance.value = null
        _beerPouring.value = null
        _beerShopFlowHandler.value = Event(BeerShopFlowHandler())
        _lockHandler.value = Event(LockHandler())
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun cleanCardData() {
        _balance.value = null
    }

    private fun setPaymentTimer() {
        paymentTimerHandler.postDelayed(paymentAction, 3000)
    }

    private fun removePaymentTimer() {
        paymentTimerHandler.removeCallbacks(paymentAction)

    }


    fun unlockCard(payment: Payment) {
        if (payment is EMoneyPayment) {
            unlockCard(payment.pointPlusId)
        }
    }


    fun unlockCard(pointPlusId: String) {
        viewModelScope.launch {
            TLogger.writeln(this.javaClass.name + "::unlockCard() START")
            try {
                if (pointPlusId.isNotBlank()) {
                    pointPlusRepository.unlockCard(pointPlusId)
                }
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            }
            TLogger.writeln(this.javaClass.name + "::unlockCard() END")
        }
    }

    fun lockCard(pointPlusId: String) {
        viewModelScope.launch {
            TLogger.writeln(this.javaClass.name + "::lockCard() START")
            try {
                if (pointPlusId.isNotBlank()) {
                    pointPlusRepository.lockCard(pointPlusId)
                }
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            }
            TLogger.writeln(this.javaClass.name + "::lockCard() END")
        }
    }

    private fun setupBeerPaymentWithNfc() {
        TLogger.writeln(this.javaClass.name + "::setupBeerPaymentWithNfc() START")
        val nfcUtil = NfcUtil(getApplication())
        if (cardTerminal == null) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES009
                    )
                )
            )
        } else {
            viewModelScope.launch {
                val cardInfo = withContext(Dispatchers.IO) {
                    var card: Card? = null

                    try {
                        cardTerminal!!.waitForCardPresent(60000)

                        card = nfcUtil.connect(cardTerminal!!)

                        val idmResponseAPDU =
                            nfcUtil.exchangeAPDU("FF CA 00 00 00", card.basicChannel)
                        val idm = Hex.toHexString(idmResponseAPDU.data)

                        Timber.tag(Logger.TAG).i("IDM: %s", idm)

                        val firstCodeResponseAPDU = nfcUtil.exchangeAPDU(
                            "FF AB 00 00 15 06 $idm 01 0B 00 04 80 00 80 01 80 02 80 03",
                            card.basicChannel
                        )
                        val firstCode = String(
                            Arrays.copyOfRange(
                                firstCodeResponseAPDU.data,
                                firstCodeResponseAPDU.data.size - 64,
                                firstCodeResponseAPDU.data.size
                            )
                        )
                        Timber.tag(Logger.TAG).i("First code: %s", firstCode)


                        val secondCodeResponseAPDU = nfcUtil.exchangeAPDU(
                            "FF AB 00 00 15 06 $idm 01 0B 00 04 80 04 80 05 80 06 80 07",
                            card.basicChannel
                        )
                        val secondCode = String(
                            Arrays.copyOfRange(
                                secondCodeResponseAPDU.data,
                                secondCodeResponseAPDU.data.size - 64,
                                secondCodeResponseAPDU.data.size
                            )
                        )
                        Timber.tag(Logger.TAG).i("Second code: %s", secondCode)


                        Timber.tag(Logger.TAG)
                            .i("%s%s", firstCode.trim(), secondCode.substring(0, 5).trim())

                        return@withContext firstCode.trim() + secondCode.substring(0, 5).trim()

                    } catch (e: Exception) {
                        Timber.i(e.toString())
                    } finally {
                        card?.disconnect(true)
                    }
                }

                if (cardInfo is String) {
                    analyzeAndSendCardInfo(cardInfo)
                } else {
                    setupBeerPaymentWithNfc()
                }
            }
        }
        TLogger.writeln(this.javaClass.name + "::setupBeerPaymentWithNfc() END")
    }

    private val taskHandler = Handler()

    private val repeatativeTaskRunnable = Runnable {
        setupBeerPaymentWithNfc()
    }

    fun startHandler() {
        taskHandler.postDelayed(repeatativeTaskRunnable, 2000)
    }

    fun stopHandler() {
        taskHandler.removeCallbacks(repeatativeTaskRunnable)
    }

    fun restartHandler() {
        /*Simulate Card check*/
        if (BuildConfig.SIMULATE_CARD) {
            setPayment(
                EMoneyPayment(
                    pointPlusId = BuildConfig.SIMULATE_CARD_MEMBER_CODE,
                    repository = pointPlusRepository,
                    cardAuthInfo = BuildConfig.SIMULATE_CARD_AUTH_INFO
                )
            )
            return
        }
        stopHandler()
        startHandler()

    }

    private fun analyzeAndSendCardInfo(cardInfo: String) {
        TLogger.writeln(this.javaClass.name + "::analyzeAndSendCardInfo() START")
        val cardUtil = CardUtil()
        val cardMemberCode = cardUtil.getCardMemberCode(cardInfo)
        if (!cardUtil.checkValidCard(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            restartHandler()

        } else if (!cardUtil.checkMemberCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.EB003
                    )
                )
            )
            restartHandler()
        } else if (!cardUtil.checkCompanyCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.EB004
                    )
                )
            )
            restartHandler()
        } else {
            setPayment(
                EMoneyPayment(
                    pointPlusId = cardMemberCode,
                    repository = pointPlusRepository,
                    cardAuthInfo = cardInfo
                )
            )
            stopHandler()
        }
        TLogger.writeln(this.javaClass.name + "::analyzeAndSendCardInfo() END")
    }

    fun unlockCard() {
        TLogger.writeln(this.javaClass.name + "::unlockCard() START")
        try {
            unlockCard(_payment.value!!.peekContent())
        } catch (ex: Exception) {
            setException(ex)
        }
        TLogger.writeln(this.javaClass.name + "::unlockCard() END")
    }

    fun lockCard() {
        TLogger.writeln(this.javaClass.name + "::lockCard() START")
        _payment.value?.peekContent().let {
            try {
                if (it is EMoneyPayment) {
                    lockCard(it.pointPlusId)
                }
            } catch (ex: Exception) {
                setException(ex)
            }
        }
        TLogger.writeln(this.javaClass.name + "::lockCard() END")
    }

    fun forceStopOnOutOfService(){
        initialEventSocket()
        viewModelScope.launch {

            //Timber.i("BeerShop Connect for forceStop")
            try {
                //Timber.i("BeerShop Connect for forceStop: obniz id not get yet")
                beers!!.value!!.map {
                    //Timber.i("BeerShop Connect for forceStop: it")
                    connectSocket()
                    mSocket.emit("forceStop", it.obnizId)

                }

            } catch (ex: Exception) {
                TLogger.writeln(this.javaClass.name + "::connectSocket() CONNECT_SOCKET_ERROR")
                Timber.i("CONNECT_SOCKET_ERROR")
                /*
                viewModel.setException(
                    MessageException(
                        MessagesModel(
                            ErrorLogCode.ES004,
                            coreMsgArgs = listOf(tabletId)
                        )
                    )
                )

                 */
            } finally {
                // setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }
}
