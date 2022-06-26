package com.nereus.craftbeer.model.printer

/**
 * Beer receipt generator
 *
 * @property receipt
 * @constructor Create empty Beer receipt generator
 */
class BeerReceiptGenerator(val receipt: SaleReceipt) : ReceiptGenerator(receipt = receipt) {

}