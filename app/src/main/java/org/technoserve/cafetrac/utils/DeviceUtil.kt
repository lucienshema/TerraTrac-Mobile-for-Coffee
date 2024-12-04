
package com.example.cafetrac.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
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