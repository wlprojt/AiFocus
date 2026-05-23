package com.codepillars.ifocus

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.codepillars.ifocus.ui.theme.IFocusTheme
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private var apps by mutableStateOf<List<AppInfo>>(emptyList())
    private var usageApps by mutableStateOf<List<AppUsageInfo>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, AppLockService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        enableEdgeToEdge()

        setContent {
            IFocusTheme {
                NavScreen(
                    apps = apps,
                    usageApps = usageApps,
                    drawableToBitmap = ::drawableToBitmap
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        apps = getInstalledApps()
        usageApps = getTodayAppUsage()
    }

    private fun getInstalledApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        return packageManager.queryIntentActivities(intent, 0)
            .map {
                AppInfo(
                    name = it.loadLabel(packageManager).toString(),
                    packageName = it.activityInfo.packageName,
                    icon = it.loadIcon(packageManager)
                )
            }
            .filter {
                it.packageName != packageName &&
                        it.packageName != "com.codepillars.ifocus"
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }

    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getTodayAppUsage(): List<AppUsageInfo> {
        val usageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager

        val calendar = java.util.Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            android.app.usage.UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        return stats
            .filter { it.totalTimeInForeground > 0 }
            .mapNotNull { usage ->
                try {
                    val appInfo = packageManager.getApplicationInfo(usage.packageName, 0)

                    AppUsageInfo(
                        appName = packageManager.getApplicationLabel(appInfo).toString(),
                        packageName = usage.packageName,
                        totalTime = usage.totalTimeInForeground,
                        icon = packageManager.getApplicationIcon(usage.packageName)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .filter {
                it.packageName != packageName &&
                        it.packageName != "com.codepillars.ifocus"
            }
            .sortedByDescending { it.totalTime }
    }

}

