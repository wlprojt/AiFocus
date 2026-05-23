package com.codepillars.ifocus


data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable
)

data class AppUsageInfo(
    val appName: String,
    val packageName: String,
    val totalTime: Long,
    val icon: android.graphics.drawable.Drawable
)