package com.nereus.craftbeer.model.pointplus.v4.transaction

import com.nereus.craftbeer.model.pointplus.v4.transaction.CommonResponseTransaction
import org.simpleframework.xml.Attribute


/**
 * Based card response transaction
 *
 * @constructor  Based card response transaction
 */
class BasedCardResponseTransaction : CommonResponseTransaction() {
    /*START from request*/

    @field:Attribute(name = "request_id_cancel", required = false)
    var requestIdCancel: String? = null

    @field:Attribute(name = "input_value", required = false)
    var inputValue: Int = 0

    @field:Attribute(name = "input_point", required = false)
    var inputPoint: Int = 0

    @field:Attribute(name = "pos_receipt_code", required = false)
    var posReceiptCode: String? = null

    @field:Attribute(name = "member_code", required = false)
    var memberCode: String? = null

    @field:Attribute(name = "card_auth_type", required = false)
    var cardAuthType: Int = 0

    @field:Attribute(name = "card_auth_info", required = false)
    var cardAuthInfo: String? = null
    /*END */

    /* START exist in request but change in response */
    @field:Attribute(name = "default_service", required = false)
    var defaultService: Int = 0

    @field:Attribute(name = "sales_amount", required = false)
    var salesAmount: Int = 0
    /* END */

    @field:Attribute(name = "auth_no", required = false)
    var authNo: String? = null

    @field:Attribute(name = "expire_ymd", required = false)
    var expireYmd: String? = null

    @field:Attribute(name = "card_name", required = false)
    var cardName: String? = null

    @field:Attribute(name = "old_value", required = false)
    var oldValue: Int = 0

    @field:Attribute(name = "value_premium", required = false)
    var valuePremium: Int = 0

    @field:Attribute(name = "discount", required = false)
    var discount: Int = 0

    @field:Attribute(name = "value", required = false)
    var value: Int = 0

    @field:Attribute(name = "new_value", required = false)
    var newValue: Int = 0

    @field:Attribute(name = "value_max", required = false)
    var valueMax: Int = 0

    @field:Attribute(name = "value_min_charge", required = false)
    var valueMinCharge: Int = 0

    @field:Attribute(name = "value_unit_charge", required = false)
    var valueUnitCharge: Int = 0

    @field:Attribute(name = "value_unit", required = false)
    var valueUnit: String? = null

    @field:Attribute(name = "value_unit_position", required = false)
    var valueUnitPosition: Int = 0

    @field:Attribute(name = "old_point", required = false)
    var oldPoint: Int = 0

    @field:Attribute(name = "converted_value", required = false)
    var convertedValue: Int = 0

    @field:Attribute(name = "point", required = false)
    var point: Int = 0

    @field:Attribute(name = "new_point", required = false)
    var newPoint: Int = 0

    @field:Attribute(name = "point_premium", required = false)
    var pointPremium: Int = 0

    @field:Attribute(name = "point_max", required = false)
    var pointMax: Int = 0

    @field:Attribute(name = "point_unit", required = false)
    var pointUnit: String? = null

    @field:Attribute(name = "point_unit_position", required = false)
    var pointUnitPosition: String? = null

    @field:Attribute(name = "activate_flag", required = false)
    var activateFlag: String? = null

    @field:Attribute(name = "converted_value_unit", required = false)
    var convertedValueUnit: String? = null

    @field:Attribute(name = "converted_value_unit_position", required = false)
    var convertedValueUnitPosition: Int = 0

    @field:Attribute(name = "card_type", required = false)
    var cardType: String? = null

    @field:Attribute(name = "need_to_confirm", required = false)
    var needToConfirm: String? = null

    @field:Attribute(name = "old_charge_value_balance", required = false)
    var oldChargeValueBalance: Int = 0

    @field:Attribute(name = "old_present_value_balance", required = false)
    var oldPresentValueBalance: Int = 0

    @field:Attribute(name = "charge_premium_value", required = false)
    var chargePremiumValue: Int = 0

    @field:Attribute(name = "present_premium_value", required = false)
    var presentPremiumValue: Int = 0

    @field:Attribute(name = "charge_value", required = false)
    var chargeValue: Int = 0

    @field:Attribute(name = "new_charge_value_balance", required = false)
    var newChargeValueBalance: Int = 0

    @field:Attribute(name = "new_premium_value_balance", required = false)
    var newPremiumValueBalance: Int = 0

    @field:Attribute(name = "old_premium_value_balance", required = false)
    var oldPremiumValueBalance: Int = 0

    @field:Attribute(name = "new_present_value_balance", required = false)
    var newPresentValueBalance: Int = 0

    @field:Attribute(name = "old_premium_point_balance", required = false)
    var oldPremiumPointBalance: Int = 0

    @field:Attribute(name = "old_payment_point_balance", required = false)
    var oldPaymentPointBalance: Int = 0

    @field:Attribute(name = "old_present_point_balance", required = false)
    var oldPresentPointBalance: Int = 0

    @field:Attribute(name = "premium_point", required = false)
    var premiumPoint: Int = 0

    @field:Attribute(name = "payment_point", required = false)
    var paymentPoint: Int = 0

    @field:Attribute(name = "present_point", required = false)
    var presentPoint: Int = 0

    @field:Attribute(name = "new_premium_point_balance", required = false)
    var newPremiumPointBalance: Int = 0

    @field:Attribute(name = "new_payment_point_balance", required = false)
    var newPaymentPointBalance: Int = 0

    @field:Attribute(name = "new_present_point_balance", required = false)
    var newPresentPointBalance: Int = 0
}