package com.codepillars.ifocus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

class LockScreenActivity : ComponentActivity() {

    private var lockedPackageName = ""

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppLockManager.isLockScreenOpen = true
        lockedPackageName = intent.getStringExtra("packageName") ?: ""

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        onBackPressedDispatcher.addCallback(this) {
            // Back disabled
        }

        setContent {
            var remaining by remember {
                mutableLongStateOf(
                    AppLockManager.getRemainingTime(
                        this@LockScreenActivity,
                        lockedPackageName
                    )
                )
            }

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.laughing_cat)
            )

            LaunchedEffect(lockedPackageName) {
                while (true) {
                    val time = AppLockManager.getRemainingTime(
                        this@LockScreenActivity,
                        lockedPackageName
                    )

                    remaining = time

                    if (time <= 0) {
                        finish()
                        break
                    }

                    delay(1000)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "App Locked",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Time left: ${remaining / 60000}:${((remaining / 1000) % 60).toString().padStart(2, '0')}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    LottieAnimation(
                        modifier = Modifier.size(200.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (AppLockManager.isLocked(this, lockedPackageName)) {
            val intent = Intent(this, LockScreenActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            intent.putExtra("packageName", lockedPackageName)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        AppLockManager.isLockScreenOpen = false
        super.onDestroy()
    }
}