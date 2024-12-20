package org.technoserve.cafetrac.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Akrabis")
data class Akrabi(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "akrabiNumber")
    val akrabiNumber: String,
    @ColumnInfo(name = "akrabiName")
    val akrabiName: String,
    @ColumnInfo(name = "siteName")
    val siteName: String,
    @ColumnInfo(name = "age")
    val age: Int = 0,
    @ColumnInfo(name = "gender")
    val gender: String = "",
    @ColumnInfo(name = "woreda")
    val woreda: String = "",
    @ColumnInfo(name = "kebele")
    val kebele: String = "",
    @ColumnInfo(name = "govtIdNumber")
    val govtIdNumber: String = "",
    @ColumnInfo(name = "phone")
    val phone: String = "",
    @ColumnInfo(name = "photoUri")
    val photoUri: String?
)
