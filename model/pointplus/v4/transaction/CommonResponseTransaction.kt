package com.nereus.craftbeer.model.pointplus.v4.transaction

import org.simpleframework.xml.Attribute

/**
 * Define XML response models for Point+ Plus API v4.0.3
 */

abstract class CommonResponseTransaction : CommonTransaction() {

    @field:Attribute(name = "auth_ymd")
    var authYmd: String? = null

    @field:Attribute(name = "auth_hms")
    var authHms: String? = null

    @field:Attribute(name = "message_log_id")
    var messageLogId: String? = null

    @field:Attribute(name = "error_code")
    var errorCode: String? = null

    @field:Attribute(name = "message1")
    var message1: String? = null

    @field:Attribute(name = "message2")
    var message2: String? = null
}