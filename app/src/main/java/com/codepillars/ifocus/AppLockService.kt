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
                foregroundApp != "com.codepillars.ifocus" &&
                foregroundApp != "android" &&
                !foregroundApp.contains("launcher") &&
                AppLockManager.isLocked(this@AppLockService, foregroundApp)
            ) {
                val intent = Intent(this@AppLockService, LockScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("packageName", foregroundApp)
                startActivity(intent)
            }

            handler.postDelayed(this, 1000)
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

        handler.post(checker)
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
        val start = end - 5000

        val events = usageStatsManager.queryEvents(start, end)
        val event = UsageEvents.Event()

        var packageName: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                packageName = event.packageName
            }
        }

        return packageName
    }
}