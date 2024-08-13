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


@Database(entities = [Farm::class, CollectionSite::class,BuyThroughAkrabi::class,DirectBuy::class,Akrabi::class], version = 6, exportSchema = true)
@TypeConverters(BitmapConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun farmsDAO(): FarmDAO
    abstract fun akrabiDao(): AkrabiDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE new_Farms (
                siteId           INTEGER NOT NULL,
                remote_id        BLOB    NOT NULL,
                farmerPhoto      TEXT    NOT NULL,
                farmerName       TEXT    NOT NULL,
                memberId         TEXT    NOT NULL,
                village          TEXT    NOT NULL,
                district         TEXT    NOT NULL,
                purchases        REAL,
                size             REAL    NOT NULL,
                latitude         TEXT    NOT NULL,
                longitude        TEXT    NOT NULL,
                coordinates      TEXT,
                synced           INTEGER NOT NULL DEFAULT 0,
                scheduledForSync INTEGER NOT NULL DEFAULT 0,
                createdAt        INTEGER NOT NULL,
                updatedAt        INTEGER NOT NULL,
                needsUpdate      INTEGER NOT NULL DEFAULT 0,
                id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                FOREIGN KEY (siteId)
                REFERENCES CollectionSites (siteId) ON UPDATE NO ACTION
                                                    ON DELETE CASCADE
            )
        """.trimIndent())

                db.execSQL("""
            INSERT INTO new_Farms (
                siteId, remote_id, farmerPhoto, farmerName, memberId,
                village, district, purchases, size, latitude, longitude,
                coordinates, synced, scheduledForSync, createdAt, updatedAt, needsUpdate, id
            )
            SELECT
                siteId, remote_id, farmerPhoto, farmerName, memberId,
                village, district, purchases, size, latitude, longitude,
                coordinates, 0 AS synced, 0 AS scheduledForSync, createdAt, updatedAt, 0 AS needsUpdate, id
            FROM Farms
        """.trimIndent())

                db.execSQL("DROP TABLE Farms")
                db.execSQL("ALTER TABLE new_Farms RENAME TO Farms")
            }
        }

        private val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // SQL statement to create the new table
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS BuyThroughAkrabi (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                location TEXT NOT NULL,
                siteName TEXT NOT NULL,
                akrabiSearch TEXT NOT NULL,
                akrabiNumber TEXT NOT NULL,
                akrabiName TEXT NOT NULL,
                cherrySold REAL NOT NULL,
                cherryPricePerKg REAL NOT NULL,
                paid REAL NOT NULL,
                photo TEXT NOT NULL
            )
        """.trimIndent())
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // SQL statement to create the new DirectBuy table
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS DirectBuy (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                location TEXT NOT NULL,
                siteName TEXT NOT NULL,
                farmerSearch TEXT NOT NULL,
                farmerNumber TEXT NOT NULL,
                farmerName TEXT NOT NULL,
                cherrySold REAL NOT NULL,
                cherryPricePerKg REAL NOT NULL,
                paid REAL NOT NULL,
                photo TEXT NOT NULL
            )
        """.trimIndent())
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS Akrabis (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        akrabiNumber TEXT NOT NULL,
                        akrabiName TEXT NOT NULL,
                        siteName TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }


        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "farm_collector_database_for_coffee_v2"
                    ) .addMigrations(MIGRATION_1_2,MIGRATION_2_4,MIGRATION_4_5,MIGRATION_5_6)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}


