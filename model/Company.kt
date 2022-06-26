package com.nereus.craftbeer.model

import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.util.getDeviceInfoPref
import com.nereus.craftbeer.util.getShopInfoPref

data class Company constructor(

    var id: String? = null,

    var companyName: String = EMPTY_STRING,

    var companyCode: String = EMPTY_STRING
) {
    companion object {
        fun fromPreferences(): Company {
            val pref = getShopInfoPref()
            val companyId = pref.getString(SHARED_PREF_COMPANY_ID, "会社名")!!
            val companyName = pref.getString(SHARED_PREF_COMPANY_NAME, "会社名")!!
            val companyCode = pref.getString(SHARED_PREF_COMPANY_CODE, "COMCODE")!!
            return Company(
                id = companyId,
                companyName = companyName,
                companyCode = companyCode
            )
        }

        fun isExisted() : Boolean {
            return !getDeviceInfoPref().getString(SHARED_PREF_COMPANY_ID, EMPTY_STRING).isNullOrBlank()
        }
    }
}




