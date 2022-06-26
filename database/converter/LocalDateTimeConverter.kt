package com.nereus.craftbeer.database.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.nereus.craftbeer.util.parseISODateTime
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

/**
 * Local date time converter
 *
 * @constructor Create empty Local date time converter
 */
class LocalDateTimeConverter {
    /**
     * To date
     *
     * @param dateString
     * @return
     */
    @TypeConverter

    fun toDate(dateString: String?): LocalDateTime? {
        return dateString?.parseISODateTime()
    }

    /**
     * To date string
     *
     * @param date
     * @return
     */
    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toISOString()
    }
}