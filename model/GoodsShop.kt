package com.nereus.craftbeer.model

import androidx.fragment.app.activityViewModels
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.DEFAULT_REDUCTION_TAX_RATE
import com.nereus.craftbeer.constant.DEFAULT_TAX_RATE
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.database.entity.SaleLogDetail
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.viewmodel.FoodShopViewModel
import androidx.fragment.app.activityViewModels
import timber.log.Timber

/**
 * Goods shop
 *
 * @property sellingPrice
 * @property goodsShortName
 * @property imageKey
 * @property imageUrl
 * @constructor Create empty Goods shop
 */
data class GoodsShop(
    val sellingPrice: Double,
    val goodsShortName: String,
    val imageKey: String?,
    val imageUrl: String?,
)

/**
 * Goods info list
 *
 * @property id
 * @property goodsCode
 * @property janCode
 * @property goodsName
 * @property goodsShortName
 * @property description
 * @property imageKey
 * @property imageUrl
 * @property sellingPrice
 * @property purchaseCost
 * @property taxReduction
 * @property taxRate
 * @property handlingFlag
 * @property soldOutFlag
 * @property goodsShop
 * @constructor Create empty Goods info list
 */
data class GoodsInfoList(
    val id: String,
    val goodsCode: String,
    val janCode: String,
    val goodsName: String?,
    val goodsShortName: String?,
    val description: String?,
    val imageKey: String?,
    val imageUrl: String?,
    val sellingPrice: Double,
    val purchaseCost: Double,
    var taxReduction: Int = 0,
    var taxRate: Double?,
    val handlingFlag: Short,
    val soldOutFlag: Short,
    val goodsShop: List<GoodsShop>?
)

/**
 * As combination goods info
 *
 * @return
 */
fun GoodsInfoList.asCombinationGoodsInfo(): CombinationGoodsInfo {
    val cFood = CombinationGoodsInfo(id = id, janCode = janCode)
    val taxRate = taxRate?: if (janCode == "0000000000000") { 0.0 } else if (taxReduction == 1) DEFAULT_REDUCTION_TAX_RATE else DEFAULT_TAX_RATE
    Timber.i("goods code: %s", goodsCode )
    Timber.i("goods code janCode: %s", janCode )

    cFood.goodsCode = goodsCode
    cFood.goodsShortName = goodsShortName ?: EMPTY_STRING
    cFood.goodsName = goodsName ?: EMPTY_STRING
    cFood.quantity = 1
    cFood.handlingFlag = handlingFlag.toInt()
    cFood.imageUrl =  goodsShop?.firstOrNull()?.imageUrl?: imageUrl?: EMPTY_STRING
    cFood.imageKey = goodsShop?.firstOrNull()?.imageKey?: imageKey?: EMPTY_STRING
    cFood.sellingPrice = goodsShop?.firstOrNull()?.sellingPrice?: sellingPrice
    cFood.soldOutFlag = soldOutFlag.toInt()
    cFood.taxReduction = taxReduction
    cFood.taxRate = taxRate
    return cFood
}

data class Data(
    val data: List<GoodsInfoList>
)

data class CombinationGoodsInfo(
    val id: String = EMPTY_STRING,
    val janCode: String = EMPTY_STRING,
    var quantity: Int = 1,
    var goodsCode: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var imageKey: String = "",
    var sellingPrice: Double = 0.0,
    var purchaseCost: Double = 0.0,
    var handlingFlag: Int = 0,
    var soldOutFlag: Int = 0,
    var goodsName: String = "",
    var goodsShortName: String = "",
    var type: ProducType = ProducType.GOODS,
    var taxRate: Double = DEFAULT_TAX_RATE,
    var taxReduction: Int = 0,
    var variousPrice: Double = 0.0
) {
    fun sellingPriceWithTax(isTakeAway: Boolean): Double {
        val price: Double = sellingPrice
        Timber.i("goods code: %s", goodsCode+":abc")
        //Timber.i("goods code: %s", janCode+":abc")
        // Timber.i("goods code: %s", sellingPrice.toString())
        // このタイミングではsellingPriceは計算に使えば手に入るが、goodsCodeやjanCodeは手に入らない？
        return if (goodsCode == "282") {
            price
        } else {
            price * (1 + getTaxRate(isTakeAway))
        }
    }

}

data class UpdateGoodsStatusRequest(
    val handlingFlag: Int
)


fun List<CombinationGoodsInfo>.buildSaleLogName(): String {
    return stream().findFirst().map {
        it.goodsName
    }.orElse(getStringResource(R.string.default_sale_log_name))
}


fun List<CombinationBeersInfo>.buildSaleLogBeerName(): String {
    return stream().findFirst().map {
        it.beerName
    }.orElse(getStringResource(R.string.default_sale_log_name))
}

fun CombinationGoodsInfo.getTaxRate(takeAway: Boolean): Double {
    return if (goodsCode == "VARIOUS") {
        0.0
    } else if (takeAway) {
        taxRate
    } else DEFAULT_TAX_RATE
}

fun List<CombinationGoodsInfo>.totalPriceWithoutTax(): Double {
    return map { it.sellingPrice * it.quantity }
        .sum()
}

fun List<CombinationGoodsInfo>.totalPriceWithTax(takeAway: Boolean): Double {
    return map { it.sellingPrice * it.quantity * (it.getTaxRate(takeAway) + 1) }
        .sum()
}

fun CombinationGoodsInfo.sellingPriceWithTax(takeAway: Boolean): Double {
    val price: Double = sellingPrice
    return price * (getTaxRate(takeAway) + 1)
}

fun List<CombinationGoodsInfo>.totalTax(takeAway: Boolean): Double {
    return map { it.sellingPrice * it.quantity * it.getTaxRate(takeAway) }.sum()
}

fun List<CombinationGoodsInfo>.updatePrice(price: Double) {
    map {
        if (it.goodsCode == "VARIOUS") {
            it.sellingPrice = price
            it.taxRate = 0.0
        }
    }
}

fun List<CombinationGoodsInfo>.totalQuantity(): Int {
    return map { it.quantity }
        .sum()
}

// Tax 8%
fun List<CombinationGoodsInfo>.subReducedTotalPriceWithoutTax(takeAway: Boolean): Double {
    return if (takeAway) 0.0 else {
        filter { it.taxReduction == 1 }
            .map { it.sellingPrice * it.quantity }
            .sum()
    }
}

// Tax 8%
fun List<CombinationGoodsInfo>.subReducedTotalTax(takeAway: Boolean): Double {
    return if (takeAway) 0.0 else {

        filter { it.taxReduction == 1 }
            .map { it.sellingPrice * it.quantity * if (it.goodsCode != "VARIOUS") {it.taxRate} else {0.0} }
            .sum()
    }
}

// Tax 10%
fun List<CombinationGoodsInfo>.subFullTotalPriceWithoutTax(takeAway: Boolean): Double {
    return if (takeAway) {
        map { it.sellingPrice * it.quantity }
            .sum()
    } else {
        filter { it.taxReduction == 0 }
            .map { it.sellingPrice * it.quantity }
            .sum()
    }
}

// Tax 10%
fun List<CombinationGoodsInfo>.subFullTotalTax(takeAway: Boolean): Double {
    return if (takeAway) {
        map { it.sellingPrice * it.quantity * if (it.goodsCode != "VARIOUS") {DEFAULT_TAX_RATE} else {0.0} }
            .sum()
    } else {
        filter { it.taxReduction == 0 }
            .map { it.sellingPrice * it.quantity * if (it.goodsCode != "VARIOUS") {DEFAULT_TAX_RATE} else {0.0} }
            .sum()
    }
}

fun List<CombinationGoodsInfo>.asSaleLogDetailEntities(saleLogId: Long): List<SaleLogDetail> {
    return map {
        SaleLogDetail(
            saleLogId = saleLogId,
            goodsId = if (it.id.isBlank()) {
                null
            } else it.id,
            tapBeerId = null,
            amount = it.quantity,
            taxRate = if(it.id == "VARIOUS") 0.0 else it.taxRate,
            purchaseCost = it.purchaseCost,
            sellingPrice = it.sellingPrice
        )
    }
}






