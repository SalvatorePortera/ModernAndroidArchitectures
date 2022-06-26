package com.nereus.craftbeer.model.pointplus.v4.transaction

import com.nereus.craftbeer.constant.FIXED_MESSAGE_VERSION
import com.nereus.craftbeer.constant.POINT_PLUS_DEFAULT_RETRY_COUNT
import com.nereus.craftbeer.enums.pointplus.v4.TransactionType
import com.nereus.craftbeer.model.pointplus.v4.transactions.Transaction
import com.nereus.craftbeer.util.toPointPlusDateFormat
import com.nereus.craftbeer.util.toPointPlusTimeFormat
import com.nereus.craftbeer.util.toBaseDateTime
import org.simpleframework.xml.Attribute
import java.time.LocalDateTime

/**
 * CommonRequestTransaction: all fields are required
 *
 */
open class CommonTransaction : Transaction() {

    @field:Attribute(name = "message_version")
    var messageVersion: String

    @field:Attribute(name = "request_type")
    lateinit var requestType: String

    @field:Attribute(name = "request_id")
    lateinit var requestId: String

    @field:Attribute(name = "client_signature")
    lateinit var clientSignature: String

    @field:Attribute(name = "transaction_type")
    var transactionType: String

    @field:Attribute(name = "retry_count")
    var retryCount: Int

    @field:Attribute(name = "terminal_ymd")
    var terminalDate: String

    @field:Attribute(name = "terminal_hms")
    var terminalTime: String

    init {
        messageVersion = FIXED_MESSAGE_VERSION
        transactionType = TransactionType.UN_RESEND.getValue()
        retryCount = POINT_PLUS_DEFAULT_RETRY_COUNT
        terminalDate = LocalDateTime.now().toBaseDateTime().toPointPlusDateFormat()
        terminalTime = LocalDateTime.now().toBaseDateTime().toPointPlusTimeFormat()
    }
}
