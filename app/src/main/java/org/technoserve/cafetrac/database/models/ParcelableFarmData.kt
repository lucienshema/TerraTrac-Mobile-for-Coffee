package com.example.cafetrac.database.models

import android.os.Parcel
import android.os.Parcelable

data class ParcelableFarmData(val farm: Farm, val view: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Farm::class.java.classLoader)!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(farm, flags)
        parcel.writeString(view)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParcelableFarmData> {
        override fun createFromParcel(parcel: Parcel): ParcelableFarmData {
            return ParcelableFarmData(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableFarmData?> {
            return arrayOfNulls(size)
        }
    }
}
