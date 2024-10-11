package com.example.egnss4coffeev2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.egnss4coffeev2.database.converters.BitmapConverter
import com.example.egnss4coffeev2.database.converters.DateConverter


@Database(entities = [Farm::class, CollectionSite::class,BuyThroughAkrabi::class,DirectBuy::class,Akrabi::class], version = 1, exportSchema = true)
@TypeConverters(BitmapConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun farmsDAO(): FarmDAO
    abstract fun akrabiDao(): AkrabiDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "farm_collector_database_for_coffee_v2"
                    ) .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}


