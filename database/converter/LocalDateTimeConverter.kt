package com.nereus.craftbeer.database.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.nereus.craftbeer.util.parseISODateTime
import com.nereus.craftbeer.util.toISOString
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return dateString?.parseISODateTime()
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toISOString()
    }
}