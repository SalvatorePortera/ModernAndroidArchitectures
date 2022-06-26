package com.nereus.craftbeer.util.printer

import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.model.Company
import com.nereus.craftbeer.model.ShopInfo
import com.nereus.craftbeer.model.printer.*

fun Receipt.getReceiptGenerator(companyInfo: Company, shopInfo: ShopInfo): ReceiptGenerator {
    company = companyInfo
    shop = shopInfo
    return when (receiptType) {
        ProducType.GOODS, ProducType.TAP_BEER -> SaleReceiptGenerator(this as SaleReceipt)
        ProducType.TOP_UP -> TopUpReceiptGenerator(this as TopUpReceipt)
        else -> throw IllegalStateException()
    }
}

fun Receipt.getIssuedReceiptGenerator(
    companyInfo: Company,
    shopInfo: ShopInfo
): IssuedReceiptGenerator {
    company = companyInfo
    shop = shopInfo
    return IssuedReceiptGenerator(this)
}
