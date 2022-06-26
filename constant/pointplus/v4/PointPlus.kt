@file:JvmName("PointPlusConstants")

package com.nereus.craftbeer.constant

import okhttp3.MediaType
import java.nio.charset.Charset

const val FIXED_MESSAGE_VERSION = "02"
const val POINT_PLUS_DEFAULT_RETRY_COUNT = 3

const val XML_SHIFT_JIS_MEDIA_TYPE = "application/xml; charset=SHIFT-JIS"
const val SHIFT_JIS_CHARSET = "SHIFT-JIS"
const val XML_SHIFT_JIS_PROLOG = "<?xml version=\"1.0\" encoding= \"Shift_JIS\" ?>"
const val POINT_PLUS_YMD_FORMAT = "yyyyMMdd"
const val POINT_PLUS_HMS_FORMAT = "HHmmss"

val REQUIRED_FIELDS_ALL = listOf<String>(
    "request_id"
)
val REQUIRED_FIELDS_CARD = listOf(
    REQUIRED_FIELDS_ALL, listOf("member_code")
).flatten()




