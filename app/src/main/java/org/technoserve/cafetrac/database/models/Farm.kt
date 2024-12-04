package org.technoserve.cafetrac.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cafetrac.database.converters.AccuracyListConvert
import com.example.cafetrac.database.converters.CoordinateListConvert
import com.example.cafetrac.database.converters.DateConverter
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

import  java.util.UUID

@Entity(
    tableName = "Farms",
    foreignKeys = [
        ForeignKey(
            entity = CollectionSite::class,
            parentColumns = ["siteId"],
            childColumns = ["siteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
@Parcelize
@TypeConverters(CoordinateListConvert::class, AccuracyListConvert::class)
data class Farm(
    @ColumnInfo(name = "siteId")
    var siteId: Long,
    @ColumnInfo(name = "remote_id")
    var remoteId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "farmerPhoto")
    var farmerPhoto: String,
    @ColumnInfo(name = "farmerName")
    var farmerName: String,
    @ColumnInfo(name = "memberId")
    var memberId: String,
    @ColumnInfo(name = "village")
    var village: String,
    @ColumnInfo(name = "district")
    var district: String,
    @ColumnInfo(name = "purchases")
    var purchases: Float?,
    @ColumnInfo(name = "size")
    var size: Float,
    @ColumnInfo(name = "latitude")
    var latitude: String,
    @ColumnInfo(name = "longitude")
    var longitude: String,
    @ColumnInfo(name = "coordinates")
    var coordinates: List<Pair<Double?, Double?>>?,
    @ColumnInfo(name = "accuracyArray")  // New field
    var accuracyArray: List<Float?>?,     // List to store accuracies
    @ColumnInfo(name = "age")
    var age: Int?,  // New field
    @ColumnInfo(name = "gender")
    var gender: String?,  // New field
    @ColumnInfo(name = "govtIdNumber")
    var govtIdNumber: String?,  // New field
    @ColumnInfo(name = "numberOfTrees")
    var numberOfTrees: Int?,  // New field
    @ColumnInfo(name = "phone")
    var phone: String?,  // New field
    @ColumnInfo(name = "photo")
    var photo: String?,  // New field
    @ColumnInfo(name = "synced", defaultValue = "0")
    val synced: Boolean = false,
    @ColumnInfo(name = "scheduledForSync", defaultValue = "0")
    val scheduledForSync: Boolean = false,
    @ColumnInfo(name = "createdAt")
    @TypeConverters(DateConverter::class)
    val createdAt: Long,
    @ColumnInfo(name = "updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long,
    @ColumnInfo(name = "needsUpdate", defaultValue = "0")
    var needsUpdate: Boolean = false,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Farm

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        UUID.fromString(parcel.readString()),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(ParcelablePair)?.map { Pair(it.first, it.second) },
        parcel.createFloatArray()?.toList(),  // Read accuracyArray as a List<Float?>
        parcel.readValue(Int::class.java.classLoader) as? Int,  // New field
        parcel.readString(),  // New field
        parcel.readString(),  // New field
        parcel.readValue(Int::class.java.classLoader) as? Int,  // New field
        parcel.readString(),  // New field
        parcel.readString(),  // New field
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
        id = parcel.readLong()
    }

    override fun describeContents(): Int = 0

    companion object : Parceler<Farm> {

        override fun Farm.write(parcel: Parcel, flags: Int) {
            parcel.writeLong(siteId)
            parcel.writeString(remoteId.toString())
            parcel.writeString(farmerPhoto)
            parcel.writeString(farmerName)
            parcel.writeString(memberId)
            parcel.writeString(village)
            parcel.writeString(district)
            parcel.writeValue(purchases)
            parcel.writeFloat(size)
            parcel.writeString(latitude)
            parcel.writeString(longitude)
            parcel.writeTypedList(coordinates?.map {
                it.first?.let { it1 ->
                    it.second?.let { it2 ->
                        ParcelablePair(
                            it1,
                            it2
                        )
                    }
                }
            })
            parcel.writeFloatArray(accuracyArray?.filterNotNull()?.toFloatArray())  // Write accuracyArray
            parcel.writeValue(age)  // New field
            parcel.writeString(gender)  // New field
            parcel.writeString(govtIdNumber)  // New field
            parcel.writeValue(numberOfTrees)  // New field
            parcel.writeString(phone)  // New field
            parcel.writeString(photo)  // New field
            parcel.writeByte(if (synced) 1 else 0)
            parcel.writeByte(if (scheduledForSync) 1 else 0)
            parcel.writeLong(createdAt)
            parcel.writeLong(updatedAt)
            parcel.writeByte(if (needsUpdate) 1 else 0)
            parcel.writeLong(id)
        }

        override fun create(parcel: Parcel): Farm {
            return Farm(parcel)
        }
    }
}
