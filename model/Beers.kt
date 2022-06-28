package com.nereus.craftbeer.model

import android.view.View
import com.nereus.craftbeer.constant.DEFAULT_TAX_RATE
import com.nereus.craftbeer.database.entity.SaleLog
import com.nereus.craftbeer.database.entity.SaleLogDetail

/**
 * Tap beer
 *
 * @property id
 * @property beerCode
 * @property beerName
 * @property beerNameEn
 * @property taxRate
 * @property beerShortName
 * @property beerType
 * @property beerStyle
 * @property ibu
 * @property alcohol
 * @property brewery
 * @property breweryEn
 * @property description
 * @property imageUrl
 * @property acidity
 * @property weight
 * @property bitter
 * @property sweet
 * @property smell
 * @property country
 * @property purchaseCost
 * @property sellingPrice
 * @property colour
 * @property tapBeerShops
 * コンストラクタ  Tap beer
 */
data class TapBeer(
    val id: String,
    val beerCode: String,
    val beerName: String,
    val beerNameEn: String,
    val taxRate: Float,
    val beerShortName: String,
    val beerType: String,
    val beerStyle: String,
    val ibu: Float,
    val alcohol: Float,
    val brewery: String,
    val breweryEn: String,
    val description: String,
    val imageUrl: String,
    val acidity: Float,
    val weight: Float,
    val bitter: Float,
    val sweet: Float,
    val smell: Float,
    val country: Country,
    val purchaseCost: Double,
    val sellingPrice: Double,
    val colour: String,
    val tapBeerShops: List<TapBeerShop>?
)

data class BeerServer(
    val id: String,
    val serverCode: String,
    val obnizId: String,
    val validFlag: Int,
    val isOnline: Int,
    val startUsingAt: String?,
    val shopId: String,
    val CompanyId: String
)

data class Country(
    val id: String,
    val countryCode: String,
    val name: String,
    val flagUrl: String,
    val region: String
)

data class TapBeerShop(
    val id: String,
    val companyId: String,
    val shopId: String,
    val tapBeerId: String,
    val purchaseCost: Double,
    val sellingPrice: Double
)

data class BeerInfoList(
    val companyId: String,
    val shopId: String,
    val tapBeerId: String,
    val tapBeer: TapBeer?,
    val originalAmount: Int,
    val remainingAmount: Int,
    val maintainFlag: Int,
    val id: String,
    val beerServer: BeerServer?,
    val outStandardDisplay: Int?
)

data class TabBeerDeviceInfoList(
    val id: String,
    val beerName: String,
)


data class BeerData(
    val data: List<BeerInfoList>
)

data class TabBeerDeviceData(
    val data: List<TabBeerDeviceInfoList>
)

data class CombinationBeersInfo constructor(
    val tapBeerId: String,
    val beerName: String,
    val beerNameEn: String,
    val taxRate: Float,
    val ibu: Float,
    val alcohol: Float,
    val brewery: String,
    val breweryEn: String,
    val description: String?,
    val imageUrl: String,
    val acidity: Float,
    val weight: Float,
    val bitter: Float,
    val sweet: Float,
    val smell: Float,
    val countryCode: String,
    val flagUrl: String,
    val purchaseCost: Double,
    val sellingPrice: Double,
    val beerStyle: String,
    val region: String?,
    var maintainFlag: Int,
    val id: String,
    var amountInMlL: Int = 0,
    val obnizId: String?,
    val outStandardDisplay: Int?,
    val originalAmount: Int?,
    val remainingAmount: Int?,
    val idObnizServer: String?,
    val colour: String,
) {
    fun getString(): String {
        return if (maintainFlag == 1) {
            "メンテナンス中"
        } else {
            "販売中"
        }
    }

    fun setVisibleLayout(): Int {
        return if (maintainFlag == 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun sellingPriceWithTax(): Double {
        return sellingPrice * (1 + DEFAULT_TAX_RATE)
    }
}

data class UpdateBeersStatusRequest(
    val maintainFlag: Int
)

data class UpdateTapBeerServerRequest(
    val beerServer: BeerServerUpdate?,
    val tapBeer: TapBeerUpdate?,
    val originalAmount: Int?,
    val remainingAmount: Int?,
    val outStandardDisplay: Int?
)

data class BeerServerUpdate(
    val id: String
)

data class TapBeerUpdate(
    val id: String
)

fun CombinationBeersInfo.asSaleLogDetailEntity(saleLogId: Long): SaleLogDetail {
    return SaleLogDetail(
        saleLogId = saleLogId,
        tapBeerId = if (tapBeerId.isNullOrBlank()) {
            null
        } else tapBeerId,
        goodsId = null,
        amount = amountInMlL,
        taxRate = taxRate.toDouble(),
        purchaseCost = purchaseCost,
        sellingPrice = sellingPrice
    )
}


fun List<CombinationBeersInfo>.asSaleLogDetailEntityBeer(
    saleLogId: Long,
    saleLog: SaleLog
): List<SaleLogDetail> {
    return map {
        SaleLogDetail(
            saleLogId = saleLogId,
            tapBeerId = if (it.tapBeerId.isNullOrBlank()) {
                null
            } else it.tapBeerId,
            goodsId = null,
            amount = saleLog.totalAmount,
            taxRate = saleLog.tax,
            purchaseCost = this[0].purchaseCost,
            sellingPrice = this[0].sellingPrice
        )
    }
}

fun List<CombinationBeersInfo>.asSaleLogDetailEntityBeerMaintain(
    saleLogId: Long,
    saleLog: SaleLog
): List<SaleLogDetail> {
    return map {
        SaleLogDetail(
            saleLogId = saleLogId,
            tapBeerId = if (it.tapBeerId.isNullOrBlank()) {
                null
            } else it.tapBeerId,
            goodsId = null,
            amount = saleLog.totalAmount,
            taxRate = 0.0,
            purchaseCost = 0.0,
            sellingPrice = 0.0
        )
    }
}











