package org.technoserve.cafetrac.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cafetrac.database.converters.DateConverter

@Entity(tableName = "CollectionSites")
data class CollectionSite(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "agentName")
    var agentName: String,
    @ColumnInfo(name = "phoneNumber")
    var phoneNumber: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "village")
    var village: String,
    @ColumnInfo(name = "district")
    var district: String,
    @ColumnInfo(name = "totalFarms")
    var totalFarms: Int = 0, // Added field for total farms
    @ColumnInfo(name = "farmsWithIncompleteData")
    var farmsWithIncompleteData: Int = 0 ,// Added field for farms with incomplete data
    @ColumnInfo(name = "createdAt")
    @TypeConverters(DateConverter::class)
    val createdAt: Long,
    @ColumnInfo(name = "updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var siteId: Long = 0L
}
