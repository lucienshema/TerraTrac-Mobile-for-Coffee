package org.technoserve.cafetrac.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cafetrac.database.converters.BitmapConverter
import com.example.cafetrac.database.converters.DateConverter
import com.example.cafetrac.database.dao.AkrabiDao
import com.example.cafetrac.database.dao.FarmDAO
import com.example.cafetrac.database.models.Akrabi
import com.example.cafetrac.database.models.BuyThroughAkrabi
import com.example.cafetrac.database.models.CollectionSite
import com.example.cafetrac.database.models.DirectBuy
import com.example.cafetrac.database.models.Farm
import org.technoserve.cafetrac.database.helpers.ContextProvider
import org.technoserve.cafetrac.database.helpers.MigrationHelper


@Database(entities = [Farm::class, CollectionSite::class, BuyThroughAkrabi::class, DirectBuy::class, Akrabi::class], version = 2, exportSchema = true)
@TypeConverters(BitmapConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun farmsDAO(): FarmDAO
    abstract fun akrabiDao(): AkrabiDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val context = ContextProvider.getContext()
                MigrationHelper(context).executeSqlFromFile(db, "migration_1_2.sql")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    ContextProvider.initialize(context)

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "farm_collector_database_for_coffee_v2"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}



