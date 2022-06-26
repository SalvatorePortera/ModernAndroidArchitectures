@file:JvmName("Constants")

package com.nereus.craftbeer.constant

import java.util.concurrent.TimeUnit

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
@JvmField
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"

@JvmField
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

// Other keys
const val TOKEN = "TOKEN"
const val ACCESS_TOKEN = "ACCESS_TOKEN"
const val RECEIPT_LIST = "RECEIPT_LIST"
const val COMPANY_ID = "COMPANY_ID"
const val SHOP_ID = "SHOP_ID"
const val BEARER = "Bearer "
const val NO_TOKEN = "NO_TOKEN"
const val GOODS_LIST = "GOODS_LIST"
const val TAG_OUTPUT = "OUTPUT"
const val PRINTER = "PRINTER"
const val PRINTER_MODEL = "PRINTER_MODEL"
const val PRINTER_TYPE = "PRINTER_TYPE"
const val PRINTER_ADDRESS = "PRINTER_ADDRESS"

const val DELAY_TIME_MILLIS: Long = 3000

// Key of age ranges
const val AGE_10 = 1
const val AGE_20 = 2
const val AGE_30 = 3
const val AGE_40 = 4
const val AGE_50 = 5

// Key of genders
const val GENDER_MALE = 1
const val GENDER_FEMALE = 2
const val GENDER_OTHER = 3

// Key of payment methods
const val PAYMENT_HOUSE_MONEY = 1
const val PAYMENT_CASH = 2
const val PAYMENT_ELECTRONIC_MONEY = 3
const val PAYMENT_CREDIR_CARD = 4
const val PAYMENT_QR = 5
const val PAYMENT_OTHERS = 6

// Printer
/**
 * Finish Application
 */
const val DIALOG_FINISH_APP = 99

/**
 * Bluetooth no support
 */
const val DIALOG_BLUETOOTH_NO_SUPPORT = 101

/**
 * Enable Wifi
 */
const val DIALOG_ENABLE_WIFI = 102

/**
 * Response request code
 */
const val RESPONSE_REQUEST_CODE = 0x0E

/**
 * Barcode length
 */
const val BARCODE_LENGTH = 10

/**
 * EMPTY_STRING
 */
const val EMPTY_STRING = ""
const val TAX_REDUCTION_PREFIX = "*"

/*
Worker sync data setup
*/
const val SYNC_REPEAT_INTERVAL = 15L
val SYNC_TIME_UNIT = TimeUnit.MINUTES
const val SHOP_INFO_SYNC_REPEAT_INTERVAL = 1L
val SHOP_INFO_SYNC_TIME_UNIT = TimeUnit.DAYS
const val WORKER_RETRY_LIMIT = 3
const val POINT_PLUS_RETRY_LIMIT = 3

//Printer
const val RECEIPT_WIDTH_DEFAULT = 140
const val RECEIPT_GRID_SIZE = 10
const val RECEIPT_LINE_SPACE = 7
const val ISSUED_RECEIPT_HEIGHT_DEFAULT = 350
const val SALE_RECEIPT_HEIGHT_DEFAULT = 320
const val TOP_UP_RECEIPT_HEIGHT_DEFAULT = 390
const val RECEIPT_TEXT_SIZE_SMALL = 5F
const val RECEIPT_TEXT_SIZE_DEFAULT = 7F
const val RECEIPT_TEXT_SIZE_LARGE = 10F
const val RECEIPT_TEXT_SIZE_XLARGE = 12F
const val RECEIPT_TAX_PRINT_LETTER_PER_LINE_DEFAULT = 6
const val RECEIPT_CODE_PREFIX = "R_"
const val SEPARATOR = "SEPARATOR"
const val SHORT_SEPARATOR = "--------"
const val LONG_SEPARATOR = "-----------"
const val GOODS_NAME_LIMIT = 15
const val UNIT_PRICE_LIMIT = 8
const val SUB_QUANTITY_LIMIT = 7
const val SUB_SECTION_PRICE_LIMIT = 23
const val ELLIPSIS = ".."
const val SINGLE_SPACE = " "
const val RECEIPT_STRING = "領収書\n"
const val BREAK_LINE = "\n"
const val IMAGE = "IMAGE"
const val TEXT = "IMAGE"
const val LOGO = "LOGO"
const val QR = "QR"
const val FORMATTED_PRICE = "%.1f円"
const val FORMATTED_SUB_QUANTITY = "x%d"
const val FORMATTED_TAX = "\\%.1f"

const val DOUBLE_DELTA_COMPARE = 0.0001
const val DEFAULT_TAX_RATE = 0.1
const val DEFAULT_REDUCTION_TAX_RATE = 0.08

const val DISPLAYED_TIMESTAMP_FORMAT = "yyyy/MM/dd HH:mm"
const val RECEIPT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val ISO_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

const val SHARED_PREF_RECEIPT_FILE = "SHARED_PREF_SHOP_FILE"
const val SHARED_PREF_SHOP_NAME = "SHARED_PREF_SHOP_NAME"
const val SHARED_PREF_SHOP_ID = "SHARED_PREF_SHOP_ID"
const val SHARED_PREF_SHOP_CODE = "SHARED_PREF_SHOP_CODE"
const val SHARED_PREF_RECEIPT_LOGO1_URL = "SHARED_PREF_RECEIPT_LOGO1_URL"
const val SHARED_PREF_RECEIPT_LOGO2_URL = "SHARED_PREF_RECEIPT_LOGO2_URL"
const val SHARED_PREF_RECEIPT_HEADER = "SHARED_PREF_RECEIPT_HEADER"
const val SHARED_PREF_RECEIPT_FOOTER = "SHARED_PREF_RECEIPT_FOOTER"
const val SHARED_PREF_SHOP_ADDRESS = "SHARED_PREF_SHOP_ADDRESS"
const val SHARED_PREF_SHOP_POSTAL_CODE = "SHARED_PREF_SHOP_POSTAL_CODE"
const val SHARED_PREF_SHOP_PHONE = "SHARED_PREF_SHOP_PHONE"
const val SHARED_PREF_COMPANY_NAME = "SHARED_PREF_COMPANY_NAME"
const val SHARED_PREF_COMPANY_ID = "SHARED_PREF_COMPANY_ID"
const val SHARED_PREF_COMPANY_CODE = "SHARED_PREF_COMPANY_CODE"
const val SHARED_PREF_RECEIPT_TAX_STAMP = "SHARED_PREF_RECEIPT_TAX_STAMP"
const val SHARED_PREF_RECEIPT_TIMESTAMP = "SHARED_PREF_RECEIPT_TIMESTAMP"
const val SHARED_PREF_BEER_POURING_CORRECTION_AMOUNT = "SHARED_PREF_BEER_POURING_CORRECTION_AMOUNT"

const val REQUEST_SCREENSHOT = 1253
val DEFAULT_LOGO = "https://s3-dev-apne1-cfbeer.s3.ap-northeast-1.amazonaws.com/upload_images/03596b52-2993-4e9b-87a2-94a025357a7d.jpeg"

const val SELECT_SCREEN_EXTRA_KEY = "SELECT_SCREEN_EXTRA"
const val SELECT_SCREEN_SPLASH_KEY = "value"
const val SELECT_SCREEN_EXTRA_FOOD_SHOP = 0
const val SELECT_SCREEN_EXTRA_MASTER_SETTING = 1
const val SELECT_SCREEN_EXTRA_SELECT_MODE = 2
const val SELECT_SCREEN_EXTRA_NFC = 3

const val PREF_DEVICE_FILE = "DEVICE"
const val PREF_DEVICE_CODE = "PREF_DEVICE_CODE"
const val PREF_DEVICE_ID = "DEVICE_ID"

const val YEN_THOUSAND_DECIMAL_FORMAT = "¥#,###,###"
const val YEN = "¥"
const val AT_SIGN = "@"
const val MINUS_SIGN = "-"
const val THOUSAND_DECIMAL_FORMAT = "#,###,###"
const val ML_THOUSAND_DECIMAL_FORMAT = "#,###,###"

const val TAP_BEER_ML_PER_TIME = 10

val PRINT_CODE = 1004
val PRINTER_VIEW_SCHEMA = "com"
val PRINTER_VIEW_HOST = "craftbeer"
val PRINTER_VIEW_PATH_PRINT_ONE = "/printOne"
val PRINTER_VIEW_PATH_PRINT_MULTIPLE = "/printMultiple"
val PRINTER_VIEW_PATH_ISSUE = "/issue"
val PRINTER_VIEW_PATH_TOP_UP = "/topup"
val PRINTER_VIEW_PATH_SALE_LOG = "/salelog"
val PRINTER_VIEW_BASE_URI = "${PRINTER_VIEW_SCHEMA}://${PRINTER_VIEW_HOST}"

//val PRINTER_URI = "siiprintagent://1.0/print?CallbackSuccess=%S&CallbackFail=%S&ErrorDialog=yes&Format=pdf&Data=${encodedUrl}&SelectOnError=no&CutType=partial&CutFeed=yes&FitToWidth=yes"
val PRINTER_SUCCESS_CODE = "200"
val PRINTER_QUERY_CODE = "Code"
val PRINTER_QUERY_MESSAGE = "Message"

val POINT_PLUS_NO_ERROR_CODE = "000"
val MESSAGE_BUNDLE_KEY = "message"
val SLASH = "/"

//Message error resposne from obniz server
val FLOWMETER_NOTFOUND = "Error: [Parts: Flowmeter] not found."
val ALREADY_CONNECTED = "is connected from 1 clients"

val DEFAULT_API_LIMIT = 500
val DEFAULT_API_PAGE = 1