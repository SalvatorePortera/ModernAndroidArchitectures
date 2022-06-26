package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.util.TLogger
import com.nereus.craftbeer.util.getDeviceInfoPref
import com.nereus.craftbeer.util.getShopInfoPref
import java.time.LocalDateTime

/**
 * Shop info
 *
 * @property id
 * @property shopName
 * @property shopCode
 * @property logo1Url
 * @property logo2Url
 * @property receiptHeader
 * @property receiptFooter
 * @property receiptStamp
 * @property address
 * @property postalCode
 * @property phoneNumber
 * @property beerPouringErrorCorrectionValue
 * @property createAt
 * @property timestamp
 * @constructor Create empty Shop info
 */
data class ShopInfo constructor(

    var id: String? = null,

    var shopName: String,

    var shopCode: String,

    var logo1Url: String,

    var logo2Url: String,

    var receiptHeader: String,

    var receiptFooter: String,

    var receiptStamp: String,

    var address: String,

    var postalCode: String,

    var phoneNumber: String,

    var beerPouringErrorCorrectionValue: Int = 0,

    var createAt: LocalDateTime? = null,

    var timestamp: String = EMPTY_STRING
) {
    companion object {
        fun fromPreferences(): ShopInfo {
            TLogger.writeln(this.javaClass.name + "::fromPreferences() START")
            val pref = getShopInfoPref()
            var si = ShopInfo(
                id = pref.getString(SHARED_PREF_SHOP_ID, "レシート印字店舗名")!!,

                shopName = pref.getString(SHARED_PREF_SHOP_NAME, "レシート印字店舗名")!!,

                shopCode = pref.getString(SHARED_PREF_SHOP_CODE, "SHOPCODE")!!,

                phoneNumber = pref.getString(SHARED_PREF_SHOP_PHONE, "電話番号")!!,

                postalCode = pref.getString(SHARED_PREF_SHOP_POSTAL_CODE, "郵便番号")!!,

                address = pref.getString(SHARED_PREF_SHOP_ADDRESS, "住所")!!,

                timestamp = pref.getString(SHARED_PREF_RECEIPT_TIMESTAMP, "2222-12-22 22:22:22")!!,

                receiptStamp = pref.getString(
                    SHARED_PREF_RECEIPT_TAX_STAMP,
                    "印紙税申告納付につき●●税務署承認済"
                )!!,

                receiptFooter = pref.getString(SHARED_PREF_RECEIPT_FOOTER, "フッタ情報")!!,

                receiptHeader = pref.getString(SHARED_PREF_RECEIPT_HEADER, "フッタ情報")!!,

                beerPouringErrorCorrectionValue = pref.getInt(
                    SHARED_PREF_BEER_POURING_CORRECTION_AMOUNT,
                    0
                ),

                logo1Url =
                pref.getString(SHARED_PREF_RECEIPT_LOGO1_URL, DEFAULT_LOGO)!!,
                logo2Url =
                pref.getString(SHARED_PREF_RECEIPT_LOGO2_URL, DEFAULT_LOGO)!!
            )
            TLogger.writeln(this.javaClass.name + "::fromPreferences() END")
            return si
            //    val receiptId = "XHJUSKFHNDJKKS"
//    val taxPrintNote = pref.getString("SHARED_PREF_TAX_STAMP_NOTE", "税務署印コメント")
        }

        fun isExisted(): Boolean {
            return !getDeviceInfoPref().getString(SHARED_PREF_SHOP_ID, EMPTY_STRING).isNullOrBlank()
        }
    }
}






