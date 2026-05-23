package com.codepillars.ifocus

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class AppAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentPackage: String? = null
    private var sessionStartTime = 0L

    private val checker = object : Runnable {
        override fun run() {
            val pkg = currentPackage

            if (pkg != null && isValidApp(pkg)) {
                checkAndLock(pkg)
            }

            handler.postDelayed(this, 500)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        handler.post(checker)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (
            pkg.contains("launcher") ||
            pkg.contains("recents") ||
            pkg == "com.android.systemui"
        ) {
            currentPackage = null
            return
        }

        if (!isValidApp(pkg)) return

        if (currentPackage != pkg) {
            currentPackage = pkg
            sessionStartTime = System.currentTimeMillis()
        }

        checkAndLock(pkg)
    }

    private fun checkAndLock(pkg: String) {
        val limitMinutes = AppLockManager.getDailyLimit(this, pkg)

        if (limitMinutes > 0) {
            val savedUsage = UsageHelper.getTodayUsageTime(this, pkg)
            val currentSession = System.currentTimeMillis() - sessionStartTime
            val totalUsage = savedUsage + currentSession
            val limitMillis = limitMinutes * 60 * 1000L

            if (totalUsage >= limitMillis) {
                AppLockManager.lockApp(this, pkg)
            }
        }

        if (
            AppLockManager.isLocked(this, pkg) &&
            !AppLockManager.isLockScreenOpen
        ) {
            openLockScreen(pkg)
        }
    }

    private fun openLockScreen(pkg: String) {
        val intent = Intent(this, LockScreenActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        intent.putExtra("packageName", pkg)
        startActivity(intent)
    }

    private fun isValidApp(pkg: String): Boolean {
        return pkg != packageName &&
                pkg != "com.codepillars.ifocus" &&
                pkg != "android" &&
                !pkg.contains("launcher") &&
                pkg != "com.android.systemui"
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        handler.removeCallbacks(checker)
        super.onDestroy()
    }
}


