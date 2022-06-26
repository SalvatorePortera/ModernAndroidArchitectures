package com.nereus.craftbeer.model.pointplus.v4.transaction

import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.enums.pointplus.v4.CardAuthType
import com.nereus.craftbeer.enums.pointplus.v4.DefaultService
import com.nereus.craftbeer.enums.pointplus.v4.RequestType
import org.simpleframework.xml.Attribute

/**
 * Define XML request models for Point+ Plus API v4.0.3
 */
open class BasedCardRequestTransaction(requestType: RequestType?) : CommonTransaction() {

    constructor() : this(null)

    @field:Attribute(name = "request_id_cancel", required = false)
    var requestIdCancel: String? = null

    @field:Attribute(name = "input_value", required = false)
    var inputValue: Int = 0

    @field:Attribute(name = "input_point", required = false)
    var inputPoint: Int = 0

    @field:Attribute(name = "pos_receipt_code", required = false)
    var posReceiptCode: String? = null

    /*A 16-digit card number printed on the face of the card*/
    @field:Attribute(name = "member_code", required = false)
    var memberCode: String? = null

    @field:Attribute(name = "card_auth_type", required = false)
    var cardAuthType: Int

    @field:Attribute(name = "card_auth_info", required = false)
    var cardAuthInfo: String? = null

    @field:Attribute(name = "is_default_service", required = false)
    var isDefaultService: Int

    @field:Attribute(name = "sales_to_calculate", required = false)
    var salesToCalculate: Int = 0

    init {
        this.requestType = requestType?.getValue() ?: EMPTY_STRING
        cardAuthType = CardAuthType.JIS2.getValue()
        isDefaultService = DefaultService.APPLY.getValue()
    }
}
