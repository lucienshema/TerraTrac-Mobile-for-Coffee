
package com.example.egnss4coffeev2.database.sync

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DeviceIdUtil {

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    private suspend fun getAdvertisingId(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                adInfo.id
            } catch (e: Exception) {
                Log.e("DeviceIdUtil", "Error getting Advertising ID", e)
                null
            }
        }
    }

    suspend fun getDeviceId(context: Context): String {
        val androidId = getAndroidId(context)
        val advertisingId = getAdvertisingId(context)

        return advertisingId ?: androidId
    }
}