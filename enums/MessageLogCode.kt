package com.nereus.craftbeer.enums

import androidx.annotation.StringRes
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.util.formatMessage
import com.nereus.craftbeer.util.getStringResource

/**
 * Message log code
 *
 * @property tabletMessageId
 * @property coreMessageId
 * コンストラクタ  Message log code
 */
enum class MessageLogCode(
    @StringRes private val tabletMessageId: Int?,
    @StringRes private val coreMessageId: Int?
) {
    /*BEER*/
    EB010(null, R.string.EB010),
    EB011(null, R.string.EB011),
    EB012(null, R.string.EB012),
    EB013(null, R.string.EB013),
    EB014(null, R.string.EB014),
    EB015(null, R.string.EB015),
    TEST(null, R.string.TEST_LOG),
    EB016(null, R.string.EB016),
    EB017(null, R.string.EB017),
    EB018(null, R.string.EB018),

    /*POINT PLUS*/
    MP001(null, R.string.MP001),

    /*CORE API*/
    MC001(null, R.string.MC001),



    UNKNOWN(R.string.Unknown_Tablet, R.string.Unknown);


    /**
     * Get tablet message
     *
     * @param args
     * @return
     */
    fun getTabletMessage(vararg args: Any): String {
        return if (tabletMessageId != null) formatMessage(
            getStringResource(tabletMessageId),
            *args
        ) else EMPTY_STRING
    }

    /**
     * Get core message
     *
     * @param args
     * @return
     */
    fun getCoreMessage(vararg args: Any): String {
        return if (coreMessageId != null) formatMessage(
            getStringResource(coreMessageId),
            *args
        ) else EMPTY_STRING
    }

    /**
     * Has core message
     *
     * @return
     */
    fun hasCoreMessage(): Boolean {
        return coreMessageId != null
    }

    /**
     * Has tablet message
     *
     * @return
     */
    fun hasTabletMessage(): Boolean {
        return tabletMessageId != null
    }
}