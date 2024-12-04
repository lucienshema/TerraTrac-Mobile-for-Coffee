package com.example.cafetrac.database.models

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
import com.example.cafetrac.database.dao.FarmDAO
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

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
        parcel.createTypedArrayList(ParcelablePair.CREATOR)?.map { Pair(it.first, it.second) },
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

data class CollectionSiteDto(
    val local_cs_id: Long,
    val name: String,
    val agent_name: String,
    val phone_number: String?,
    val email: String?,
    val village: String?,
    val district: String?
)

data class FarmDetailDto(
    val remote_id: String,
    val farmer_name: String,
    val member_id: String,
    val village: String,
    val district: String,
    val size: Float,
    val latitude: Double,
    val longitude: Double,
    val coordinates: List<List<Double?>>?,
    val accuracies: List<Float?>?,
)

data class DeviceFarmDto(
    val device_id: String,
    val collection_site: CollectionSiteDto,
    val farms: List<FarmDetailDto>
)


fun List<Farm>.toDeviceFarmDtoList(deviceId: String, farmDao: FarmDAO): List<DeviceFarmDto> {
    return this.groupBy { it.siteId } // Group by siteId
        .mapNotNull { (siteId, farms) ->
            val collectionSite = farmDao.getCollectionSiteById(siteId) ?: return@mapNotNull null

            // Map the collection site details
            val collectionSiteDto = CollectionSiteDto(
                local_cs_id = collectionSite.siteId,
                name = collectionSite.name,
                agent_name = collectionSite.agentName ?: "Unknown",
                phone_number = collectionSite.phoneNumber,
                email = collectionSite.email,
                village = collectionSite.village,
                district = collectionSite.district
            )

            // Map the farms
            val farmDtos = farms.mapNotNull { farm ->
                farm.remoteId?.let { remoteId ->
                    // Ensure latitude and longitude are not empty or null before parsing
                    val latitude = farm.latitude.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
                    val longitude = farm.longitude.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0

                    FarmDetailDto(
                        remote_id = remoteId.toString(),
                        farmer_name = farm.farmerName,
                        member_id = farm.memberId,
                        village = farm.village,
                        district = farm.district,
                        size = farm.size,
                        latitude = latitude,
                        longitude = longitude,
                        coordinates = farm.coordinates?.map { listOf(it.first, it.second) } ?: emptyList() ,// Convert coordinate pairs
                        accuracies = farm.accuracyArray?.filterNotNull() // Filter out null values
                    )
                }
            }

            DeviceFarmDto(
                device_id = deviceId,
                collection_site = collectionSiteDto,
                farms = farmDtos
            )
        }
}

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

@Entity(tableName = "BuyThroughAkrabi")
data class BuyThroughAkrabi(
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
    @ColumnInfo(name = "akrabiSearch")
    val akrabiSearch: String,
    @ColumnInfo(name = "akrabiNumber")
    val akrabiNumber: String,
    @ColumnInfo(name = "akrabiName")
    val akrabiName: String,
    @ColumnInfo(name = "cherrySold")
    val cherrySold: Double,
    @ColumnInfo(name = "cherryPricePerKg")
    val pricePerKg: Double,
    @ColumnInfo(name = "paid")
    val paid: Double,
    @ColumnInfo(name="photo")
    val photo: String, // Assuming photo is stored as a file path or URL
    @ColumnInfo(name="photoUri")
    val photoUri: String ?
)


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

//@PrimaryKey(autoGenerate = true) val id: Long = 0L,
//@ColumnInfo(name = "akrabiNumber") val akrabiNumber: String,
//@ColumnInfo(name = "akrabiName") val akrabiName: String,
//@ColumnInfo(name = "siteName") val siteName: String,
//@ColumnInfo(name = "age") val age: Int = 0, // Added age field
//@ColumnInfo(name = "gender") val gender: String = "", // Added gender field
//@ColumnInfo(name = "woreda") val woreda: String = "", // Added Woreda field
//@ColumnInfo(name = "kebele") val kebele: String = "", // Added Kebele field
//@ColumnInfo(name = "govtIdNumber") val govtIdNumber: String = "", // Added Govt ID number field
//@ColumnInfo(name = "phone") val phone: String = "", // Added phone field
//@ColumnInfo(name = "photoUri") val photoUri: String ?


