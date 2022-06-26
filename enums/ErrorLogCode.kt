package com.nereus.craftbeer.enums

import androidx.annotation.StringRes
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.formatMessage
import com.nereus.craftbeer.util.getStringResource

/**
 * Error log code
 *
 * @property tabletMessageId
 * @property coreMessageId
 * @constructor Create empty Error log code
 */
enum class ErrorLogCode(
    @StringRes private val tabletMessageId: Int?,
    @StringRes private val coreMessageId: Int?
) {
    /*Business Error*/
    EB001(null, R.string.EB001),
    EB002(R.string.EB002_Tablet, null),
    EB003(R.string.EB003_Tablet, null),
    EB004(R.string.EB004_Tablet, null),
    EB005(R.string.EB005_Tablet, null),
    EB006(R.string.EB006_Tablet, null),
    EB010(null, R.string.EB010),
    EB011(null, R.string.EB011),
    EB012(null, R.string.EB012),
    EB013(null, R.string.EB013),
    EB014(null, R.string.EB014),
    EB015(null, R.string.EB015),

    /*System Error*/
    ES001(null, R.string.ES001),
    ES005(R.string.ES005_Tablet, null),
    ES009(R.string.ES009_Tablet, null),
    ES004(R.string.ES004_Tablet, R.string.ES004),
    ES011(R.string.ES011_Tablet, R.string.ES011),
    ES010(R.string.ES010_Tablet, R.string.ES010),
    ES006(R.string.ES006_Tablet, R.string.ES006),
    ES007(R.string.ES007_Tablet, R.string.ES007),
    ES008(R.string.ES008_Tablet, R.string.ES008),

    /*Unknown error*/
    ESCOMMON(R.string.ESCOMMON_Tablet, R.string.ESCOMMON),

    /*Unknown error*/
    UNKNOWN(R.string.Unknown_Tablet, R.string.Unknown);


    fun getTabletMessage(vararg args: Any): String {
        return if (tabletMessageId != null) formatMessage(
            getStringResource(tabletMessageId),
            *args
        ) else EMPTY_STRING
    }

    fun getCoreMessage(vararg args: Any): String {
        return if (coreMessageId != null) formatMessage(
            getStringResource(coreMessageId),
            *args
        ) else EMPTY_STRING
    }

    fun hasCoreMessage(): Boolean {
        return coreMessageId != null
    }

    fun hasTabletMessage(): Boolean {
        return tabletMessageId != null
    }
}