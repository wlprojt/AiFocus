package com.codepillars.ifocus

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val MAIN = "main"
    const val APP_LOCK = "app_lock"
    const val USAGE = "usage"
    const val APP_LIMIT = "app_limit"
}

@Composable
fun NavScreen(
    apps: List<AppInfo>,
    usageApps: List<AppUsageInfo>,
    drawableToBitmap: (Drawable) -> Bitmap
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        composable(Routes.MAIN) {
            MainScreen(
                onAppLockClick = {
                    navController.navigate(Routes.APP_LOCK)
                },
                onAppUsageClick = {
                    navController.navigate(Routes.USAGE)
                },
                onAppLimitClick = {
                    navController.navigate(Routes.APP_LIMIT)
                }
            )
        }

        composable(Routes.APP_LOCK) {
            AppLockScreen(
                navController,
                apps = apps,
                drawableToBitmap = drawableToBitmap
            )
        }

        composable(Routes.USAGE) {
            UsageScreen(
                navController,
                apps = usageApps,
                drawableToBitmap = drawableToBitmap
            )
        }

        composable(Routes.APP_LIMIT) {
            AppLimitScreen(
                navController,
                apps = apps,
                drawableToBitmap = drawableToBitmap
            )
        }
    }
}