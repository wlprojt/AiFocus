package com.codepillars.ifocus


import android.content.Context

object AppLockManager {

    private const val PREF = "locked_apps"
    var isLockScreenOpen = false

    fun lockApp(context: Context, packageName: String) {
        if (isLocked(context, packageName)) return

        val endTime = System.currentTimeMillis() + 30 * 60 * 1000L

        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putLong(packageName, endTime)
            .apply()
    }

    fun isLocked(context: Context, packageName: String): Boolean {
        val endTime = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getLong(packageName, 0L)

        return System.currentTimeMillis() < endTime
    }

    fun getRemainingTime(context: Context, packageName: String): Long {
        val endTime = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getLong(packageName, 0L)

        return (endTime - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun setDailyLimit(context: Context, packageName: String, minutes: Int) {
        context.getSharedPreferences("app_limits", Context.MODE_PRIVATE)
            .edit()
            .putInt(packageName, minutes)
            .apply()
    }

    fun getDailyLimit(context: Context, packageName: String): Int {
        return context.getSharedPreferences("app_limits", Context.MODE_PRIVATE)
            .getInt(packageName, 0)
    }
}