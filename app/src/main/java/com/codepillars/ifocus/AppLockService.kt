package com.codepillars.ifocus


import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class AppLockService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private val checker = object : Runnable {
        override fun run() {
            val foregroundApp = getForegroundApp()

            if (
                foregroundApp != null &&
                foregroundApp != this@AppLockService.packageName &&
                foregroundApp != "android" &&
                !foregroundApp.contains("launcher")
            ) {
                val limitMinutes = AppLockManager.getDailyLimit(
                    this@AppLockService,
                    foregroundApp
                )

                if (limitMinutes > 0) {
                    val usedTime = getTodayUsageTime(foregroundApp)
                    val limitMillis = limitMinutes * 60 * 1000L

                    if (
                        usedTime >= limitMillis &&
                        !AppLockManager.isLocked(this@AppLockService, foregroundApp)
                    ) {
                        AppLockManager.lockApp(this@AppLockService, foregroundApp)
                    }
                }

                if (AppLockManager.isLocked(this@AppLockService, foregroundApp)) {
                    val intent = Intent(this@AppLockService, LockScreenActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                    intent.putExtra("packageName", foregroundApp)
                    startActivity(intent)
                }
            }

            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val channelId = "app_lock_service"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "App Lock Service",
                android.app.NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setContentTitle("Focus Lock Running")
            .setContentText("Checking locked apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        handler.postDelayed(checker, 500)
    }

//    override fun onCreate() {
//        super.onCreate()
//        handler.post(checker)
//    }

    override fun onDestroy() {
        handler.removeCallbacks(checker)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getForegroundApp(): String? {
        val usageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val end = System.currentTimeMillis()
        val start = end - 10_000

        val events = usageStatsManager.queryEvents(start, end)
        val event = UsageEvents.Event()

        var lastForegroundApp: String? = null
        var lastTime = 0L

        while (events.hasNextEvent()) {
            events.getNextEvent(event)

            if (
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                event.eventType == UsageEvents.Event.ACTIVITY_RESUMED
            ) {
                if (event.timeStamp > lastTime) {
                    lastTime = event.timeStamp
                    lastForegroundApp = event.packageName
                }
            }
        }

        return lastForegroundApp
    }

    private fun getTodayUsageTime(packageName: String): Long {
        val usageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = java.util.Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

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