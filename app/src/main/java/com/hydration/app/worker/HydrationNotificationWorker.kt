package com.hydration.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hydration.app.data.HydrationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HydrationNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = HydrationDatabase.getDatabase(applicationContext)
        val waterLogDao = database.waterLogDao()

        // Check if user logged water in the last 30 minutes
        val thirtyMinutesAgo = System.currentTimeMillis() - (30 * 60 * 1000)
        val recentLogs = waterLogDao.getLogsForTimeRangeSync(thirtyMinutesAgo, System.currentTimeMillis())

        if (recentLogs.isEmpty()) {
            // No recent log, show reminder
            NotificationHelper.showHydrationReminder(applicationContext)
        } else {
            // User logged water recently within 30 mins, postpone reminder (WorkManager will retry or next scheduled runs later)
        }

        Result.success()
    }
}
