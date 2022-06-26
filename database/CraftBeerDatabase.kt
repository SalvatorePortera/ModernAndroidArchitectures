package com.nereus.craftbeer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nereus.craftbeer.constant.CommonConst
import com.nereus.craftbeer.database.converter.LocalDateTimeConverter
import com.nereus.craftbeer.database.dao.*
import com.nereus.craftbeer.database.entity.*

/**
 * Craft beer database
 *
 * @constructor Create empty Craft beer database
 */
@Database(entities = [SaleLog::class, Goods::class, SaleLogDetail::class, TopUp::class, ErrorLog::class], version =  14, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class CraftBeerDatabase : RoomDatabase() {
    abstract val saleLogDao: SaleLogDao
    abstract val errorLogDao: ErrorLogDao
    abstract val topUpDao: TopUpDao
    abstract val saleLogDetailDao: SaleLogDetailDao
    abstract val saleLogListDao: SaleLogListDao
    abstract val goodsDao: GoodsDao

    companion object {
        @Volatile
        private var INSTANCE: CraftBeerDatabase? = null

        fun getInstance(context: Context): CraftBeerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CraftBeerDatabase::class.java,
                        CommonConst.DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return instance
            }
        }
    }
}