package org.technoserve.cafetrac

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.technoserve.cafetrac.database.sync.SyncWorker
import java.util.concurrent.TimeUnit

class CafeTrac : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeWorkManager()
    }

    private fun initializeWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(2, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_work_tag",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}