package com.codepillars.ifocus


import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

object UsageHelper {

    fun getTodayUsageTime(
        context: Context,
        packageName: String
    ): Long {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        return stats.firstOrNull {
            it.packageName == packageName
        }?.totalTimeInForeground ?: 0L
    }
}