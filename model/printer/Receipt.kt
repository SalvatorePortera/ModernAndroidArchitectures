package com.nereus.craftbeer.model.printer

import android.os.Build
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.PaymentMethod
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.toBoolean
import com.nereus.craftbeer.util.toDisplayedDateTimeString
import com.nereus.craftbeer.util.toDeviceDateTime
import java.time.LocalDateTime

/**
 * Receipt
 *
 * @property id
 * @property isSelected
 * @property countPrinted
 * @property isIssued
 * @property shopId
 * @property companyId
 * @property shop
 * @property company
 * @property pointPlusId
 * @property balanceBefore
 * @property balanceAfter
 * @property receiptCode
 * @property receiptType
 * @property paymentMethod
 * @property isFirstPrinting
 * @property createdAt
 * コンストラクタ  Receipt
 */
abstract class Receipt constructor(

    open var id: String = EMPTY_STRING,

    var isSelected: Boolean = false,

    var countPrinted: Short = 0,

    var isIssued: Boolean = false,

    var shopId: String = EMPTY_STRING,

    var companyId: String = EMPTY_STRING,

    var shop: ShopInfo? = null,

    var company: Company? = null,

    var pointPlusId: String = EMPTY_STRING,

    var balanceBefore: Int = 0,

    var balanceAfter: Int = 0,

    var receiptCode: String = EMPTY_STRING,

    var receiptType: ProducType? = null,

    var paymentMethod: PaymentMethod? = null,

    var isFirstPrinting: Boolean = true,

    var createdAt: LocalDateTime? = null
) {
    abstract fun getTotal(): Double

    abstract fun getReceiptName(): String

    abstract fun getReceiptPayment(): ReceiptPayment

    fun getFormatCreatedAt(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createdAt?.toDisplayedDateTimeString() ?: EMPTY_STRING
        } else {
            EMPTY_STRING
        }
    }
}

class SaleReceipt : Receipt {
    var goods: List<CombinationGoodsInfo> = ArrayList()

    var isTakeAway: Boolean = true

    private var totalSellingPrice: Double = 0.0

    private var tax: Double = 0.0

    // Build Receipt From Search Receipt History (Re-printing)
    constructor(saleLog: SaleLog) {
        id = saleLog.id
        goods = saleLog.saleLogDetails.asGoodsList()
        createdAt = saleLog.createdAt.toDeviceDateTime()
        receiptCode = saleLog.receiptCode ?: EMPTY_STRING
        isTakeAway = saleLog.takeAway.toInt() == 1
        totalSellingPrice = saleLog.totalSellingPrice
        tax = saleLog.tax
        pointPlusId = saleLog.pointPlusId
        balanceBefore = saleLog.balanceBefore
        balanceAfter = saleLog.balanceAfter
        companyId = saleLog.companyId
        shopId = saleLog.shopId
        receiptType = saleLog.getProductType()
        isFirstPrinting = saleLog.countPrinted.compareTo(0) == 0
        paymentMethod = PaymentMethod.getByValue(saleLog.paymentMethod)
        countPrinted = saleLog.countPrinted
        isIssued = saleLog.isIssued.toBoolean()
    }

    // Print Receipt after payment (First Printing)
    constructor(
        saleLog: com.nereus.craftbeer.database.entity.SaleLog,
        goods: List<CombinationGoodsInfo>
    ) {
        id = EMPTY_STRING
        this.goods = goods
        createdAt = saleLog.createdAt.toDeviceDateTime()
        receiptCode = saleLog.receiptCode
        isTakeAway = saleLog.takeAway.toInt() == 1
        totalSellingPrice = saleLog.totalSellingPrice
        tax = saleLog.tax
        pointPlusId = saleLog.pointPlusId
        balanceBefore = saleLog.balanceBefore
        balanceAfter = saleLog.balanceAfter
        companyId = saleLog.companyId
        shopId = saleLog.shopId
        receiptType = ProducType.GOODS
        isFirstPrinting = true
        paymentMethod = PaymentMethod.getByValue(saleLog.paymentMethod)
    }

    override fun getTotal(): Double {
        return totalSellingPrice
    }

    override fun getReceiptName(): String {
        return goods.firstOrNull()?.let {
            if (it.goodsName.isNullOrBlank()) it.goodsShortName else it.goodsName
        } ?: String.format(getStringResource(R.string.default_receipt_name), id)
    }

    override fun getReceiptPayment(): ReceiptPayment {
        return when (paymentMethod) {
            PaymentMethod.PAYMENT_HOUSE_MONEY -> PointPlusReceiptPayment(this)
            PaymentMethod.PAYMENT_CASH -> CashReceiptPayment(this)
            else -> DefaultReceiptPayment(this)
        }
    }
}

class TopUpReceipt : Receipt {
    var amount: Int = 0
    var deposit: Int = 0
    var change: Int = 0

    constructor(topUp: TopUp) {
        id = topUp.id
        createdAt = topUp.createdAt?.toDeviceDateTime()
        receiptCode = topUp.receiptCode ?: EMPTY_STRING
        amount = topUp.amount
        deposit = topUp.deposit
        change = topUp.change
        pointPlusId = topUp.pointPlusId
        balanceBefore = topUp.balanceBefore
        balanceAfter = topUp.balanceAfter
        companyId = topUp.companyId
        shopId = topUp.shopId
        receiptType = ProducType.TOP_UP
        isFirstPrinting = topUp.countPrinted.compareTo(0) == 0
        paymentMethod = PaymentMethod.getByValue(topUp.paymentMethod!!)
        countPrinted = topUp.countPrinted
        isIssued = topUp.isIssued.toBoolean()
    }

    constructor(topUp: com.nereus.craftbeer.database.entity.TopUp) {
        id = EMPTY_STRING
        createdAt = topUp.createdAt.toDeviceDateTime()
        receiptCode = topUp.receiptCode
        amount = topUp.amount
        deposit = topUp.deposit
        change = topUp.change
        pointPlusId = topUp.pointPlusId
        balanceBefore = topUp.balanceBefore
        balanceAfter = topUp.balanceAfter
        companyId = topUp.companyId
        shopId = topUp.shopId
        receiptType = ProducType.TOP_UP
        isFirstPrinting = true // After top-up
        paymentMethod = PaymentMethod.getByValue(topUp.paymentMethod)
    }

    override fun getTotal(): Double {
        return amount.toDouble()
    }

    override fun getReceiptName(): String {
        return String.format(getStringResource(R.string.formatedTopUpName), amount)
    }

    override fun getReceiptPayment(): ReceiptPayment {
        return when (paymentMethod) {
            PaymentMethod.PAYMENT_CASH -> TopUpCashReceiptPayment(this)
            else -> TopUpDefaultReceiptPayment(this)
        }
    }
}

fun List<Receipt>?.hasAnySelected(): Boolean {
    return this?.any { it.isSelected } ?: false
}






