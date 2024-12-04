package org.technoserve.cafetrac.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cafetrac.database.converters.DateConverter
import java.time.LocalDate

@Entity(tableName = "DirectBuy")
data class DirectBuy(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "date")
    @TypeConverters(DateConverter::class)
    val date: LocalDate,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "siteName")
    val siteName: String,
    @ColumnInfo(name = "farmerSearch")
    val farmerSearch: String,
    @ColumnInfo(name = "farmerNumber")
    val farmerNumber: String,
    @ColumnInfo(name = "farmerName")
    val farmerName: String,
    @ColumnInfo(name = "cherrySold")
    val cherrySold: Double,
    @ColumnInfo(name = "cherryPricePerKg")
    val pricePerKg: Double,
    @ColumnInfo(name = "paid")
    val paid: Double,
    @ColumnInfo(name="photo")
    val photo: String,// Assuming photo is stored as a file path or URL
    @ColumnInfo(name="photoUri")
    val photoUri: String ?
)
