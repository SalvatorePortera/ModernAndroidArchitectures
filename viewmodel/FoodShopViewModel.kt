package com.nereus.craftbeer.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.crashlytics.internal.Logger
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.activity.cardTerminal
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.constant.PRINTER_SUCCESS_CODE
import com.nereus.craftbeer.database.entity.asCombinationGoodsInfo
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.enums.pointplus.v4.RequestType
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.keyboard.KeyPad
import com.nereus.craftbeer.model.payment.*
import com.nereus.craftbeer.model.pointplus.v4.transactions.BasedCardRequestTransactions
import com.nereus.craftbeer.model.pointplus.v4.transactions.fillChargeValueRequest
import com.nereus.craftbeer.model.printer.*
import com.nereus.craftbeer.repository.*
import com.nereus.craftbeer.util.*
import com.nereus.craftbeer.util.keyboard.input
import com.nereus.craftbeer.util.livedata.Event
import com.nereus.craftbeer.util.printer.getIssuedReceiptGenerator
import com.nereus.craftbeer.util.printer.getReceiptGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.Callable
import javax.smartcardio.Card
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

/**
 * フードショップビューモデル
 *
 * @property state
 * @property goodsRepository
 * @property saleLogRepository
 * @property topUpRepository
 * @property pointPlusRepository
 * @property shopRepository
 * @property _errorLogRepository
 * @constructor
 *
 * @param application
 */
class FoodShopViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    application: Application,
    private val goodsRepository: GoodsRepository,
    private val saleLogRepository: SaleLogRepository,
    private val topUpRepository: TopUpRepository,
    val pointPlusRepository: PointPlusRepository,
    val shopRepository: ShopRepository,
    override var _errorLogRepository: ErrorLogRepository
) :
    BaseViewModel(application) {

    private val _foods = MutableLiveData<List<CombinationGoodsInfo>>()
    private val _holdFoods = MutableLiveData<List<CombinationGoodsInfo>>()
    private val _receipts = MutableLiveData<List<Receipt>>()
    var printingReceipts: Queue<Receipt> = LinkedList()
    private val _foodShopFlowHandler = MutableLiveData<Event<FoodShopFlowHandler>>()

    lateinit var context: Context

    private val _processingItemPosition = MutableLiveData<Int>()
    private val _customerAttribute = MutableLiveData<CustomerAttribute>()
    private val _topUp = MutableLiveData<TopUp>()
    private val _goodsInput = MutableLiveData<GoodsInput>()
    private val _cashReceived = MutableLiveData<Int>()
    private val _topUpDeposit = MutableLiveData<Int>()
    private val _topUpAmount = MutableLiveData<Int>()
    private val _printerResponse = MutableLiveData<Event<PrinterResponse>>()

    private val _payment = MutableLiveData<Event<Payment>>()
    private val _paymentStrategy = MutableLiveData<PaymentStrategy>()

    private val _printReceiptHistoryModel = MutableLiveData<Event<PrintReceiptHistoryModel>>()
    private val _printReceiptByDateTimeModel = MutableLiveData<Event<PrintReceiptByDateTimeModel>>()
    private val _currentBarcode = MutableLiveData<Event<String>>()

    private val _balance = MutableLiveData<Int>()

    private val _freeAmount = MutableLiveData<Int>()

    val foods: LiveData<List<CombinationGoodsInfo>>
        get() = this._foods

    val receipts: LiveData<List<Receipt>>
        get() = this._receipts

    val foodShopFlowHandler: LiveData<Event<FoodShopFlowHandler>>
        get() = this._foodShopFlowHandler

    val customerAttribute: LiveData<CustomerAttribute>
        get() = this._customerAttribute

    val topUp: LiveData<TopUp>
        get() = this._topUp

    val payment: LiveData<Event<Payment>>
        get() = this._payment

    val printReceiptHistoryModel: LiveData<Event<PrintReceiptHistoryModel>>
        get() = this._printReceiptHistoryModel

    val printReceiptByDateTimeModel: LiveData<Event<PrintReceiptByDateTimeModel>>
        get() = this._printReceiptByDateTimeModel

    val processingItemPosition: LiveData<Int>
        get() = this._processingItemPosition

    val cashReceived: LiveData<Int>
        get() = this._cashReceived

    val topUpDeposit: LiveData<Int>
        get() = this._topUpDeposit

    val topUpAmount: LiveData<Int>
        get() = this._topUpAmount

    val currentBarcode: LiveData<Event<String>>
        get() = this._currentBarcode

    val holdFoods: LiveData<List<CombinationGoodsInfo>>
        get() = this._holdFoods

    val printerResponse: LiveData<Event<PrinterResponse>>
        get() = this._printerResponse

    val balance: LiveData<Int>
        get() = this._balance

    val freeAmount: LiveData<Int>
        get() = this._freeAmount


    val total: LiveData<Int> = _customerAttribute.combineWith(_foods) { c, f ->
        f!!.totalPriceWithTax(c!!.isTakeAway!!).roundToInt()
    }

    val totalBeforeTax: LiveData<Int> = foods.switchMap {
        MutableLiveData(
            it.totalPriceWithoutTax().roundToInt()
        )
    }

    val totalTax: LiveData<Int> = _customerAttribute.combineWith(_foods) { c, f ->
        f!!.totalTax(c?.isTakeAway!!).roundToInt()
    }

    val quantity: LiveData<Int> = foods.switchMap {
        MutableLiveData<Int>(
            it.totalQuantity()
        )
    }

    val paymentCashChange: LiveData<Int> = _cashReceived.switchMap {
        var change = it - total.value!!
        change = if (change > 0) change else 0
        MutableLiveData<Int>(change)
    }

    val topUpCashChange: LiveData<Int> = _topUpDeposit.switchMap {
        var change = it - _topUpAmount.value!!
        change = if (change > 0) change else 0
        MutableLiveData<Int>(change)
    }

    /**
     * 設定 age range
     *
     * @param ageRange
     */
    fun setAgeRange(ageRange: Int? = null) {
        Timber.i("============= ageRange")
        _customerAttribute.value?.ageRange = ageRange?.toShort()
    }

    /**
     * 開始日の設定
     *
     * @param calendar
     */
    fun setDateFrom(calendar: Calendar) {
        _printReceiptByDateTimeModel.value = Event(
            PrintReceiptByDateTimeModel(
                startTime = LocalDateTime.ofInstant(
                    calendar.toInstant(),
                    ZoneId.systemDefault()
                ),
                endTime = _printReceiptByDateTimeModel.value?.peekContent()?.endTime
            )
        )
    }

    /**
     * 締切日の設定
     *
     * @param calendar
     */
    fun setDateTo(calendar: Calendar) {
        _printReceiptByDateTimeModel.value = Event(
            PrintReceiptByDateTimeModel(
                endTime = LocalDateTime.ofInstant(
                    calendar.toInstant(),
                    ZoneId.systemDefault()
                ),
                startTime = _printReceiptByDateTimeModel.value?.peekContent()?.startTime
            )
        )
    }

    /**
     * 男女設定
     *
     * @param gender
     */
    fun setGender(gender: Int? = null) {
        _customerAttribute.value?.gender = gender?.toShort()
    }

    /**
     * 支払方法設定
     *
     * @param method
     */
    fun setPaymentMethod(method: Int? = null) {
        _customerAttribute.value?.paymentMethod = method?.toShort()
    }

    /**
     * 設定テイクアウト
     *
     * @param isTakeOut
     */
    fun setIsTakeOut(isTakeOut: Boolean?) {
        if (isTakeOut == null) {
            _customerAttribute.value!!.isTakeAway = null
            return
        }
        val old = _customerAttribute.value!!
        val newCus = CustomerAttribute(
            ageRange = old.ageRange,
            paymentMethod = old.paymentMethod,
            gender = old.gender,
            isTakeAway = isTakeOut
        )
        _customerAttribute.value = newCus
    }

    /**
     * 設定 topup point plus
     *
     * @param pointPlusId
     * @param cardAuthInfo
     */
    fun setTopupPointPlus(pointPlusId: String, cardAuthInfo: String) {
        _topUp.value?.let {
            it.pointPlusId = pointPlusId
            it.cardAuthInfo = cardAuthInfo
            it.amount = _topUpAmount.value!!
        }
    }

    /**
     * 設定 topup method
     *
     * @param method
     */
    fun setTopupMethod(method: Int? = null) {
        _topUp.value?.paymentMethod = method?.toShort()
    }

    /**
     * 設定 barcode
     *
     * @param barcode
     */
    fun setBarcode(barcode: String) {
        _goodsInput.value?.barcode = barcode
    }

    /**
     * 設定 product cd
     *
     * @param productCd
     */
    fun setProductCd(productCd: String) {
        _goodsInput.value?.productCd = productCd
    }

    /**
     * 設定 current barcode
     *
     * @param barcode
     */
    fun setCurrentBarcode(barcode: String) {
        Timber.i("=============setCurrentBarcode: %s", barcode)
        _currentBarcode.value = Event(barcode)
    }

    /**
     * 設定 payment
     *
     * @param payment
     */
    fun setPayment(payment: Payment) {
        _payment.value = Event(payment)
    }

    /**
     * 設定 payment strategy
     *
     * @param strategy
     */
    fun setPaymentStrategy(strategy: PaymentStrategy) {
        _paymentStrategy.value = strategy
    }

    /**
     * 設定 point plus id
     *
     * @param pointPlusId
     */
    fun setPointPlusId(pointPlusId: String) {
        _printReceiptHistoryModel.value = Event(PrintReceiptHistoryModel(pointPlusId))
    }

    /**
     * 設定 printer response
     *
     * @param printerResponse
     */
    fun setPrinterResponse(printerResponse: PrinterResponse) {
        _printerResponse.value = Event(printerResponse)
    }

    /**
     * Reset goods input
     *
     */
    fun resetGoodsInput() {
        _goodsInput.value?.barcode = ""

    }

    /**
     * Reset free amount
     *
     */
    fun resetFreeAmount() {
        _freeAmount.value = 0
    }

    /**
     * Clear cart
     *
     */
    fun clearCart() {
        _foods.postValue(ArrayList())
    }

    /**
     * Hold cart
     *
     */
    fun holdCart() {
        // Save current session
        if (_holdFoods.value!!.isEmpty()) {
            _holdFoods.value = _foods.value
            clearCart()
        } else { // Load stored session
            _foods.postValue(_holdFoods.value)
            _holdFoods.value = ArrayList()
        }
    }

    init {
        initializeCart()
    }

    /**
     * Initialize cart
     *
     */
    private fun initializeCart() {
        _foods.value = ArrayList()
        _holdFoods.value = ArrayList()
        _receipts.value = ArrayList()
        _customerAttribute.value = CustomerAttribute()
        _topUp.value = TopUp()
        _goodsInput.value = GoodsInput()
        _currentBarcode.value = Event(EMPTY_STRING)
        _foodShopFlowHandler.value = Event(FoodShopFlowHandler())
        _cashReceived.value = 0
        _processingItemPosition.value = 0
        _topUpAmount.value = 0
        _topUpDeposit.value = 0
        _freeAmount.value = 0
    }

    /**
     * Init printer by date time dialog
     *
     */
    fun initPrinterByDateTimeDialog() {
        _receipts.postValue(ArrayList())
        _printReceiptByDateTimeModel.value = Event(PrintReceiptByDateTimeModel())
    }

    /**
     * Init printer dialog
     *
     */
    fun initPrinterDialog() {
        _receipts.postValue(ArrayList())
    }

    /**
     * Reset cart
     *
     */
    private fun resetCart() {
        _foods.value = ArrayList()
        _receipts.value = ArrayList()
        _topUp.value = TopUp()
        _goodsInput.value = GoodsInput()
        _currentBarcode.value = Event(EMPTY_STRING)
        _foodShopFlowHandler.value = Event(FoodShopFlowHandler())
        _cashReceived.value = 0
        _processingItemPosition.value = 0
    }

    /**
     * Gen list
     *
     * @return
     */
    private fun genList(): List<CombinationGoodsInfo> {
        val foods = ArrayList<CombinationGoodsInfo>()
        (1..4).forEach { _ ->
            foods.add(genFood(genRandomString(6), genRandomString(4)))
        }
        return foods
    }

    /**
     * Check food exist in cart
     *
     * @param barcode
     * @return
     */
    private fun checkFoodExistInCart(barcode: String): Boolean {
        val itemIndex =
            _foods.value?.indexOfFirst { f -> barcode.equals(f.janCode, ignoreCase = true) } ?: -1
        return itemIndex != -1
    }

    /**
     * Check food exist in cart by product code
     *
     * @param productCode
     * @return
     */
    private fun checkFoodExistInCartByProductCode(productCode: String): String? {
        val goods =
            _foods.value?.firstOrNull { f -> productCode.equals(f.goodsCode, ignoreCase = true) }
        return goods?.janCode
    }

    /**
     * Add food
     *
     * @param addedFood
     */
    private fun addFood(addedFood: CombinationGoodsInfo) {
        Timber.i("addFood addedFood")
        _foods.value?.toMutableList()?.let { newList ->
            newList.add(addedFood)
            _processingItemPosition.value = newList.size - 1
            _foods.value = newList
        }
    }

    /**
     * Add food
     *
     * @param barcode
     */
    private fun addFood(barcode: String) {
        Timber.i("addFood barcode: %s", barcode )
        /**
         * カートのリストを取得する
         */
        val newList = _foods.value?.toMutableList()

        /**
         * カートに同一アイテムがあるかを確認する
         * あればIndex、なければ-1が返る
         */
        val itemIndex = newList?.indexOfFirst { f -> f.janCode == barcode }

        /**
         * カートに同一アイテムがなければ追加する
         */
        if (itemIndex == -1) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    goodsRepository.getGoods(barcode)?.let {
                        val cFood = CombinationGoodsInfo(id = it.goodsId, janCode = barcode)
                        cFood.goodsCode = it.goodsCode
                        cFood.goodsShortName = it.goodsShortName
                        cFood.goodsName = it.goodsShortName
                        cFood.quantity = 1
                        cFood.handlingFlag = it.handlingFlag.toInt()
                        cFood.imageUrl = it.imageUrl
                        cFood.imageKey = it.imageKey
                        cFood.sellingPrice = it.sellingPrice
                        cFood.soldOutFlag = it.soldOutFlag.toInt()
                        cFood.taxReduction = it.taxReduction
                        cFood.taxRate = it.taxRate
                        cFood
                    }
                }?.let { addFood(it) }
            }
        } else {
            newList?.let { newList ->
                itemIndex?.let {
                    Timber.d("------------ new food added before")
                    Timber.d(newList[it].imageUrl)
                    newList[it] = newList[it].copy(quantity = newList[it].quantity + 1)
                    Timber.d("------------ new food added after")
                    Timber.d(newList[it].imageUrl)
                    _processingItemPosition.value = itemIndex!!
                }
                _foods.value = newList
            }
        }

    }

    /**
     * Update food
     *
     * @param food
     */
    fun updateFood(food: CombinationGoodsInfo) {
        val newList = _foods.value?.toMutableList()
        val itemIndex = newList?.indexOfFirst { f -> f.janCode == food.janCode }
        itemIndex?.let {
            newList.let { newList ->
                newList[itemIndex] = food
                _processingItemPosition.value = itemIndex!!
                _foods.value = newList
            }
        }
    }

    /**
     * Remove food
     *
     * @param food
     */
    fun removeFood(food: CombinationGoodsInfo) {
        val newList = _foods.value?.toMutableList()
        newList?.let { newList ->
            newList.removeIf { f -> f.janCode == food.janCode }
            _foods.value = newList

        }
    }

    /**
     * Gen food
     *
     * @param janCode
     * @param name
     * @return
     */
    fun genFood(janCode: String?, name: String): CombinationGoodsInfo {
        val jan = janCode ?: genRandomString(6)
        val food = CombinationGoodsInfo(
            janCode = jan
        )
        food.goodsShortName = name
        food.quantity = 1
        food.goodsName = genRandomString(12)
        return food
    }

    /**
     * Get shop info
     *
     * @param shopId
     * @return
     */
    private suspend fun getShopInfo(shopId: String? = null): ShopInfo {
        return if (shopId.isNullOrBlank()) {
            ShopInfo.fromPreferences()
        } else {
            shopRepository.getShop(shopId = shopId)
        }
    }

    /**
     * Get company info
     *
     * @param companyId
     * @return
     */
    private suspend fun getCompanyInfo(companyId: String? = null): Company {
        return if (companyId.isNullOrBlank()) {
            Company.fromPreferences()
        } else {
            shopRepository.getCompany(companyId = companyId)
        }
    }

    /**
     * Print next receipt
     *
     * @param receipt
     */
    private suspend fun printNextReceipt(receipt: Receipt) {
        withContext(Dispatchers.IO) {
            receipt.getReceiptGenerator(
                getCompanyInfo(receipt.companyId),
                getShopInfo(receipt.shopId)
            ).generate()
        }.let { document ->
            context.printReceipt(
                document,
                isMultiplePrinting = true,
                isTopUp = receipt.receiptType == ProducType.TOP_UP,
                receiptId = receipt.id
            )
        }
    }

    /**
     * Issue next receipt
     *
     * @param receipt
     */
    private suspend fun issueNextReceipt(receipt: Receipt) {
        withContext(Dispatchers.IO) {
            receipt.getIssuedReceiptGenerator(
                getCompanyInfo(receipt.companyId),
                getShopInfo(receipt.shopId)
            ).generate()
        }.let { document ->
            context.printReceipt(
                document,
                isMultiplePrinting = true,
                isTopUp = receipt.receiptType == ProducType.TOP_UP,
                isIssued = true,
                receiptId = receipt.id
            )
        }
    }

    /**
     * Print receipts
     *
     */
    fun printReceipts() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                printingReceipts = LinkedList(_receipts.value!!.filter { it.isSelected })
                printNextReceipt(printingReceipts.poll()!!)
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
     * Issue receipts
     *
     */
    fun issueReceipts() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                printingReceipts = LinkedList(_receipts.value!!.filter { it.isSelected })
                issueNextReceipt(printingReceipts.poll()!!)
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
     * Print receipt
     *
     * @param receipt
     * @param receiptId
     */
    private suspend fun printReceipt(receipt: Receipt, receiptId: String) {
        ///!!! DEBUG only
        if (BuildConfig.DEBUG) {
            return
        }
        val doc = withContext(Dispatchers.IO) {
            receipt.getReceiptGenerator(
                getCompanyInfo(receipt.companyId),
                getShopInfo(receipt.shopId)
            )
                .generate()
        }
        context.printReceipt(doc, receiptId = receiptId)
    }

    /**
     * Select receipt history
     *
     * @param receipt
     */
    fun selectReceiptHistory(receipt: Receipt) {
        _receipts.value?.filter { it.id == receipt.id }
            ?.first()
            ?.let {
                it.isSelected = receipt.isSelected
            }
    }

    /**
     * Pay
     *
     */
    fun pay() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            _payment.value?.let {
                it.peekContent().total = total.value!!
                Timber.i("pay(): %s", it.peekContent().toString())
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
                    val result =
                        _paymentStrategy.value!!.pay(it.peekContent())

                    _foodShopFlowHandler.postValue(
                        Event(
                            FoodShopFlowHandler(
                                isPaymentSuccess = result.isSuccess,
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
        }
    }

    /**
     * Topup
     *
     */
    fun topup() {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            _topUp.value?.let {
                try {
                    state["example"] = "example"
                    Timber.i("=== 設定 example? %s", state["example"])

                    val company = withContext(Dispatchers.IO) {
                        getCompanyInfo()
                    }
                    val shop = withContext(Dispatchers.IO) {
                        getShopInfo()
                    }

                    _topUp.value!!.apply {
                        receiptCode = genReceiptCode(company.companyCode, shop.shopCode)
                    }

                    val result = chargeValue(_topUp.value!!)
                    _foodShopFlowHandler.postValue(
                        Event(
                            FoodShopFlowHandler(
                                isTopUpSuccess = result.isSuccess,
                                topUpResult = result
                            )
                        )
                    )
                } catch (ex: MessageException) {
                    ex.setCallback(Callable { topup() })
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    unlockCard(_topUp.value!!.pointPlusId)
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
        }
    }

    /**
     * Charge value
     *
     * @param topUp
     * @return
     */
    private suspend fun chargeValue(topUp: TopUp): TopUpResult {
        val transactions = BasedCardRequestTransactions(RequestType.VALUE_CHARGE)
        transactions.fillChargeValueRequest(
            poinPlusId = topUp.pointPlusId,
            amount = topUp.amount,
            receiptCode = topUp.receiptCode!!,
            cardAuthInfo = topUp.cardAuthInfo
        )
        val response = withContext(Dispatchers.IO) {
            pointPlusRepository.callCardApi(transactions)
        }
        Timber.d("new balance is%s", response.newChargeValueBalance)
        return TopUpResult(
            balanceBefore = response.oldChargeValueBalance,
            balanceAfter = response.newChargeValueBalance,
            amount = topUp.amount
        )
    }

    /**
     * Search sale logs
     *
     * @param pointPlusId
     */
    fun searchSaleLogs(pointPlusId: String?) {
        pointPlusId?.let {
            viewModelScope.launch {
                setLoadingState(CommonConst.LOADING_VISIBLE)
                try {
                    val fromSaleLogs = saleLogRepository.getMany(pointPlusId = it)
                        .asReceipts()

                    val fromTopUps = topUpRepository.getMany(pointPlusId = it)
                        .asReceipts()

                    _receipts.postValue(
                        (fromSaleLogs + fromTopUps).sortedByDescending { it.createdAt }
                    )
                } catch (ex: MessageException) {
                    ex.setCallback(Callable { searchSaleLogs(pointPlusId = pointPlusId) })
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
        }
    }

    /**
     * Search sale logs
     *
     * @param printReceiptByDateTimeModel
     */
    fun searchSaleLogs(printReceiptByDateTimeModel: PrintReceiptByDateTimeModel?) {
        printReceiptByDateTimeModel?.let {
            if (it.startTime == null || it.endTime == null) {
                return
            }
            if (it.endTime!!.isBefore(it.startTime)) {
                setException(MessageException(MessagesModel(R.string.msg_invalid_date_period)))
                return
            }
            viewModelScope.launch {
                setLoadingState(CommonConst.LOADING_VISIBLE)
                try {
                    val fromSaleLogs =
                        saleLogRepository.getMany(startTime = it.startTime, endTime = it.endTime)
                            .asReceipts()

                    val fromTopUps =
                        topUpRepository.getMany(startTime = it.startTime, endTime = it.endTime)
                            .asReceipts()

                    _receipts.postValue(
                        (fromSaleLogs + fromTopUps).sortedByDescending { it.createdAt }
                    )
                } catch (ex: MessageException) {
                    ex.setCallback { searchSaleLogs(printReceiptByDateTimeModel) }
                    setException(ex)
                } catch (ex: Exception) {
                    setException(ex)
                } finally {
                    setLoadingState(CommonConst.LOADING_GONE)
                }
            }
        }
    }

    /**
     * Handle post payment
     *
     */
    fun handlePostPayment() {
        viewModelScope.launch {
            try {
                val saleLog = buildSaleLog()
                val saleLogId = withContext(Dispatchers.IO) {
                    saleLogRepository.sendSaleLog(saleLog, _foods.value!!)
                }
                val receipt = SaleReceipt(
                    saleLog, foods.value!!
                )
                printReceipt(receipt, receiptId = saleLogId)
                resetCart()
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            }
        }
    }

    /**
     * Unlock card
     *
     * @param payment
     */
    fun unlockCard(payment: Payment) {
        if (payment is EMoneyPayment) {
            unlockCard(payment.pointPlusId)
        }
    }

    /**
     * Unlock card
     *
     * @param pointPlusId
     */
    fun unlockCard(pointPlusId: String) {
        viewModelScope.launch {
            try {
                if (pointPlusId.isNotBlank()) {
                    pointPlusRepository.unlockCard(pointPlusId)
                }
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            }
        }
    }

    /**
     * Handle post top up
     *
     * @param topUpResult
     */
    fun handlePostTopUp(topUpResult: TopUpResult) {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                val topUp = buildTopUp(topUpResult)
                val receipt = TopUpReceipt(topUp)
                val doc = withContext(Dispatchers.IO) {
                    receipt.getReceiptGenerator(
                        getCompanyInfo(receipt.companyId),
                        getShopInfo(receipt.shopId)
                    )
                        .generate()
                }
                val topUpId = withContext(Dispatchers.IO) {
                    topUpRepository.sendTopUp(topUp)
                }
                context.printReceipt(doc, receiptId = topUpId, isTopUp = true)
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
     * Build sale log
     *
     * @return
     */
    private fun buildSaleLog(): com.nereus.craftbeer.database.entity.SaleLog {
        val pointPlusId = _payment.value?.let {
            if (it.peekContent() is EMoneyPayment) {
                (it.peekContent() as EMoneyPayment).pointPlusId
            } else EMPTY_STRING
        } ?: EMPTY_STRING

        val attribute = _customerAttribute.value!!

        val takeAway = if (attribute.isTakeAway!!) 1 else 0

        return com.nereus.craftbeer.database.entity.SaleLog(
            pointPlusId = pointPlusId,
            balanceAfter = getPaymentResult().balanceAfter,
            balanceBefore = getPaymentResult().balanceBefore,
            saleLogName = _foods.value!!.buildSaleLogName(),
            paymentMethod = attribute.paymentMethod!!,
            totalSellingPrice = _foods.value!!.totalPriceWithTax(attribute.isTakeAway!!),
            totalAmount = _foods.value!!.totalQuantity(),
            tax = _foods.value!!.totalTax(attribute.isTakeAway!!),
            productType = ProducType.GOODS.getValue(),
            createdAt = LocalDateTime.now().toBaseDateTime(),
            takeAway = takeAway.toShort(),
            receiptCode = _payment.value?.peekContent()?.receiptCode ?: EMPTY_STRING,
            companyId = Company.fromPreferences().id!!,
            shopId = ShopInfo.fromPreferences().id!!
        )
    }

    /**
     * Build top up
     *
     * @param balance
     * @return
     */
    private fun buildTopUp(balance: TopUpResult): com.nereus.craftbeer.database.entity.TopUp {
        val topUp = _topUp.value!!

        return com.nereus.craftbeer.database.entity.TopUp(
            pointPlusId = topUp.pointPlusId,
            balanceBefore = balance.balanceBefore,
            balanceAfter = balance.balanceAfter,
            deposit = _topUpDeposit.value!!,
            change = topUpCashChange.value!!,
            amount = topUp.amount,
            topUpName = topUp.buildTopUpName(),
            paymentMethod = topUp.paymentMethod!!,
            receiptCode = topUp.receiptCode!!,
            createdAt = LocalDateTime.now().toBaseDateTime(),
            companyId = Company.fromPreferences().id!!,
            shopId = ShopInfo.fromPreferences().id!!
        )
    }

    /**
     * Get payment result
     *
     * @return
     */
    private fun getPaymentResult(): PaymentResult =
        _foodShopFlowHandler.value!!.peekContent().paymentResult


    /**
     * Input payment deposit
     *
     * @param keyPadValue
     */
    fun inputPaymentDeposit(keyPadValue: KeyPad.KeyPadValue) {
        try {
            val newAmount = input(_cashReceived.value.toString(), keyPadValue)
            _cashReceived.postValue(newAmount)
        } catch (ex: NumberFormatException) {
            setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
            Timber.e(ex)
        }
    }

    /**
     * Input free amount
     *
     * @param keyPadValue
     */
    fun inputFreeAmount(keyPadValue: KeyPad.KeyPadValue) {
        Timber.i("キーパッド入力")
        try {
            val newAmount = input(_freeAmount.value.toString(), keyPadValue)
            _freeAmount.postValue(newAmount)
            Timber.i("キーパッド入力: %d" , newAmount)
        } catch (ex: NumberFormatException) {
            setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
            Timber.e(ex)
            Timber.i("キーパッド入力　エラーらしい")
        }
    }


    /**
     * Input top up amount
     *
     * @param keyPadValue
     */
    fun inputTopUpAmount(keyPadValue: KeyPad.KeyPadValue) {
        try {
            val newAmount = input(_topUpAmount.value.toString(), keyPadValue)
            _topUpAmount.postValue(newAmount)
        } catch (ex: NumberFormatException) {
            setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
            Timber.e(ex)
        }
    }

    /**
     * Input top up deposit
     *
     * @param keyPadValue
     */
    fun inputTopUpDeposit(keyPadValue: KeyPad.KeyPadValue) {
        try {
            val newAmount = input(_topUpDeposit.value.toString(), keyPadValue)
            _topUpDeposit.postValue(newAmount)
        } catch (ex: NumberFormatException) {
            setException(MessageException(MessagesModel(R.string.msg_internal_exception)))
            Timber.e(ex)
        }
    }

    /**
     * Handle product code
     *
     * @param productCode
     */
    fun handleProductCode(productCode: String) {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                // Check if already existed in cart
                checkFoodExistInCartByProductCode(productCode)?.let { barcode ->
                    Timber.i("アイテムがカートにありました: %s", barcode)
                    addFood(barcode)
                    return@launch
                }

                Timber.i("アイテムをオンラインに見に行きます")
                // Search online first
                try {
                    withContext(Dispatchers.IO) {
                        goodsRepository.searchGoods(productCode)
                    }?.let {
                        addFood(it)
                        return@launch
                    }
                } catch (ex: Exception) {
                    // Log Exception
                    setException(ex)

                    // Search offline
                    withContext(Dispatchers.IO) {
                        goodsRepository.getGoodsByProductCode(productCode)
                    }?.let {
                        addFood(it.asCombinationGoodsInfo())
                        return@launch
                    }
                }

                // Not found
                throw MessageException(MessagesModel(R.string.msg_product_not_exist))
            } catch (ex: MessageException) {
                setException(ex)
            } catch (ex: Exception) {
                setException(ex)
            } finally {
                Timber.i("アイテムのローディング終了だよ")
                setLoadingState(CommonConst.LOADING_GONE)
            }
        }
    }

    /**
     * Handle barcode
     *
     * @param barcode
     */
    fun handleBarcode(barcode: String) {
        if (barcode.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                // Check if already existed in cart
                if (checkFoodExistInCart(barcode)) {
                    addFood(barcode)
                    return@launch
                }

                // Search online first
                try {
                    withContext(Dispatchers.IO) {
                        goodsRepository.searchGoods(barcode)
                    }?.let {
                        addFood(it)
                        return@launch
                    }
                } catch (ex: Exception) {
                    // Log Exception
                    setException(ex)

                    // Search offline
                    withContext(Dispatchers.IO) {
                        goodsRepository.getGoods(barcode)
                    }?.let {
                        addFood(it.asCombinationGoodsInfo())
                        return@launch
                    }
                }
                // Not found
                throw MessageException(MessagesModel(R.string.msg_product_not_exist))
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
     * Print next
     *
     * @param printerResponse
     */
    fun printNext(printerResponse: PrinterResponse) {
        when (printerResponse.code) {
            PRINTER_SUCCESS_CODE -> handlePrintSuccess(printerResponse)
            else -> handlePrintError(printerResponse)
        }
    }

    /**
     * Handle print error
     *
     * @param printerResponse
     */
    private fun handlePrintError(printerResponse: PrinterResponse) {
        setException(
            MessageException(
                MessagesModel(
                    ErrorLogCode.ES008,
                    coreMsgArgs = listOf(printerResponse.code, printerResponse.message)
                )
            )
        )
    }

    /**
     * Handle print success
     *
     * @param printerResponse
     */
    private fun handlePrintSuccess(printerResponse: PrinterResponse) {
        viewModelScope.launch {
            setLoadingState(CommonConst.LOADING_VISIBLE)
            try {
                printingReceipts.poll()?.let {
                    if (printerResponse.isIssued) {
                        issueNextReceipt(it)
                    } else {
                        printNextReceipt(it)
                    }
                }

                if (printerResponse.receiptId.isNotBlank()) {
                    if (printerResponse.isTopUp) {
                        if (printerResponse.isIssued) {
                            topUpRepository.setTopupsIssued(printerResponse.receiptId)
                        } else {
                            topUpRepository.setTopupsPrinted(printerResponse.receiptId)
                        }
                    } else {
                        if (printerResponse.isIssued) {
                            saleLogRepository.setSaleLogsIssued(printerResponse.receiptId)
                        } else {
                            saleLogRepository.setSaleLogsPrinted(printerResponse.receiptId)
                        }
                    }
                }
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
     * Stop and start handler top up
     *
     */
    private fun stopAndStartHandlerTopUp() {
        stopHandlerTopUp()
        startHandlerTopUp()
    }

    /**
     * Setup top up with nfc
     *
     */
    private fun setupTopUpWithNfc() {
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
                    setupTopUpInfo(cardInfo)
                } else {
                    setupTopUpWithNfc()
                }
            }
        }

    }

    /**
     * Setup search p p with nfc
     *
     */
    private fun setupSearchPPWithNfc() {
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

                        return@withContext firstCode.trim() + secondCode.substring(0, 5).trim()

                    } catch (e: Exception) {
                        Timber.i(e.toString())
                    } finally {
                        card?.disconnect(true)
                    }
                }

                if (cardInfo is String) {
                    setupSearchInfo(cardInfo)
                } else {
                    setupSearchPPWithNfc()
                }
            }
        }

    }


    /**
     * Setup house money checkout
     *
     */
    private fun setupHouseMoneyCheckout() {
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
                    setupHouseMoneyInfo(cardInfo)
                } else {
                    setupHouseMoneyCheckout()
                }
            }
        }

    }

    /**
     * Setup top up info
     *
     * @param cardInfo
     */
    private fun setupTopUpInfo(cardInfo: String) {
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
            stopAndStartHandlerTopUp()
        } else if (!cardUtil.checkMemberCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerTopUp()
        } else if (!cardUtil.checkCompanyCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerTopUp()
        } else {
            setTopupPointPlus(pointPlusId = cardMemberCode, cardAuthInfo = cardInfo)
            topup()
            stopHandlerTopUp()
        }
    }

    /**
     * Setup search info
     *
     * @param cardInfo
     */
    private fun setupSearchInfo(cardInfo: String) {
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
            stopAndStartHandlerSearch()
        } else if (!cardUtil.checkMemberCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerSearch()
        } else if (!cardUtil.checkCompanyCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerSearch()
        } else {
            setPointPlusId(cardMemberCode)
            stopHandlerSearch()
        }
    }

    /**
     * Stop and start handler search
     *
     */
    private fun stopAndStartHandlerSearch() {
        stopHandlerSearch()
        startHandlerSearch()
    }

    /**
     * Stop and start handler checkout
     *
     */
    private fun stopAndStartHandlerCheckout() {
        stopHandlerCheckout()
        startHandlerCheckout()
    }

    /**
     * Setup house money info
     *
     * @param cardInfo
     */
    private fun setupHouseMoneyInfo(cardInfo: String) {
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
            stopAndStartHandlerCheckout()
        } else if (!cardUtil.checkMemberCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerCheckout()
        } else if (!cardUtil.checkCompanyCode(cardInfo)) {
            setException(
                MessageException(
                    MessagesModel(
                        ErrorLogCode.ES006
                    )
                )
            )
            stopAndStartHandlerCheckout()
        } else {
            setPayment(
                EMoneyPayment(
                    pointPlusId = cardMemberCode,
                    repository = pointPlusRepository,
                    cardAuthInfo = cardInfo
                )
            )
            stopHandlerCheckout()
        }
    }

    private val taskHandler = Handler()

    /**
     * Top up by nfc
     */
    private val topUpByNfc = Runnable {
        /*Simulate Card check*/
        if (BuildConfig.SIMULATE_CARD)
            setupTopUpInfo(BuildConfig.SIMULATE_CARD_AUTH_INFO)
        else
            setupTopUpWithNfc()
    }

    /**
     * Start handler top up
     *
     */
    fun startHandlerTopUp() {
        taskHandler.postDelayed(topUpByNfc, 2000)
    }

    /**
     * Stop handler top up
     *
     */
    fun stopHandlerTopUp() {
        taskHandler.removeCallbacks(topUpByNfc)
    }

    /**
     * Search by nfc
     */
    private val searchByNfc = Runnable {
        setupSearchPPWithNfc()
    }

    /**
     * Start handler search
     *
     */
    fun startHandlerSearch() {
        taskHandler.postDelayed(searchByNfc, 2000)
    }

    /**
     * Stop handler search
     *
     */
    fun stopHandlerSearch() {
        taskHandler.removeCallbacks(searchByNfc)
    }
    /**
     * Checkout house money by nfc
     */
    private val checkoutHouseMoneyByNfc = Runnable {
        setupHouseMoneyCheckout()
    }

    /**
     * Start handler checkout
     *
     */
    fun startHandlerCheckout() {
        taskHandler.postDelayed(checkoutHouseMoneyByNfc, 2000)
    }

    /**
     * Stop handler checkout
     *
     */
    fun stopHandlerCheckout() {
        taskHandler.removeCallbacks(checkoutHouseMoneyByNfc)
    }

    /**
     * Is enable on hold btn
     *
     * @param goods
     * @param holdFoods
     * @return
     */
    fun isEnableOnHoldBtn(
        goods: List<CombinationGoodsInfo>,
        holdFoods: List<CombinationGoodsInfo>
    ): Boolean {
        return goods.isEmpty() xor holdFoods.isEmpty()
    }
}
