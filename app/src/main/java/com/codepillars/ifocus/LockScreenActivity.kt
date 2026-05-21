package com.codepillars.ifocus


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

class LockScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageName = intent.getStringExtra("packageName") ?: ""

        setContent {
            var remaining by remember {
                mutableLongStateOf(AppLockManager.getRemainingTime(this, packageName))
            }

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.laughing_cat)
            )

            LaunchedEffect(Unit) {
                while (remaining > 0) {
                    delay(1000)
                    remaining = AppLockManager.getRemainingTime(this@LockScreenActivity, packageName)
                }

                finish()
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

//                    Button(onClick = {
//                        moveTaskToBack(true)
//                    }) {
//                        Text("Go Back")
//                    }
                }
            }
        }
    }
}