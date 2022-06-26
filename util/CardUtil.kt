package com.nereus.craftbeer.util

import com.nereus.craftbeer.BuildConfig

class CardUtil {

    /**
     * @Param scannedCode code which is scanned from the PP card
     * Return Company Code which is extracted from scanned code
     */
    fun getCompanyCode(scannedCode: String): String {
        return scannedCode.substring(6, 10)
    }


    /**
     * @Param scannedCode  code which is scanned from the PP card
     * Return attached data which is extracted from scanned code
     */
    fun getAttachedData(scannedCode: String): String {
        return scannedCode.substring(27, 68)
    }

    fun getPreFixMemberCode(scannedCode: String): String {
        return scannedCode.substring(10, 23)
    }

    /**
     * @Param scannedCode  code which is scanned from the PP card
     * Return Member Card Code which is extracted from scanned code
     */
    fun getCardMemberCode(scannedCode: String): String {
        return scannedCode.substring(10, 26)
    }

    /**
     * @Param cardNumber cardNumber is extracted from the scanned code
     * Return the check digit known as CD
     *
     */

    fun getCheckDigit(cardNumber: String): Int {
        val checkNum = cardNumber.substring(0, 15)
        var Z: Int
        var X = 0
        var Y = 0
        for (i in checkNum.indices) {
            val checkDigit: Char = checkNum[i]
            val parsedDigit: Int = Character.getNumericValue(checkDigit)
            if (i % 2 == 0) {
                X += parsedDigit
            } else {
                Y += parsedDigit
            }
        }
        Z = X + Y * 3
        return if (Z % 10 == 0) {
            0
        } else {
            (10 - (Z % 10))
        }
    }

     fun checkValidCard(cardNumber: String): Boolean {
        val cardMemberCode : String = getCardMemberCode(cardNumber)
        val calDigit: Int = getCheckDigit(cardMemberCode)
        val confirmDigit: Int = cardMemberCode.substring(15, 16).toInt()
        return calDigit == confirmDigit
    }

     fun checkCompanyCode(cardNumber: String): Boolean {
        return getCompanyCode(cardNumber).equals(BuildConfig.COMPANY_CODE)
    }

     fun checkMemberCode(cardNumber: String): Boolean {
//        return getPreFixMemberCode(cardNumber).equals(BuildConfig.PREFIX_MEMBER_CODE)
         // Currently no need to check member code
        return true
    }

}