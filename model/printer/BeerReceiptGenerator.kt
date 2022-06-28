package com.nereus.craftbeer.model.printer

/**
 * Beer receipt generator
 *
 * @property receipt
 * コンストラクタ  Beer receipt generator
 */
class BeerReceiptGenerator(val receipt: SaleReceipt) : ReceiptGenerator(receipt = receipt) {

}