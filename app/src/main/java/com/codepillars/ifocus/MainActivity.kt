package com.codepillars.ifocus

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codepillars.ifocus.ui.theme.IFocusTheme
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    data class AppInfo(
        val name: String,
        val packageName: String,
        val icon: android.graphics.drawable.Drawable
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, AppLockService::class.java))
        } else {
            startService(Intent(this, AppLockService::class.java))
        }

        enableEdgeToEdge()
        setContent {
            IFocusTheme {

                val apps = remember { getInstalledApps() }
                var selectedApp by remember { mutableStateOf<AppInfo?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "iFocus",
                                    style = MaterialTheme.typography.headlineLarge
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "* Permission required for lock the app",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Allow Usage Access")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:$packageName")
                                        )
                                        startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Allow Overlay Permission")
                                }
                            }
                        }

                        Text(
                            text = "Installed Apps",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(apps) { app ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable {
                                            selectedApp = app
                                        },
                                    elevation = CardDefaults.cardElevation(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Image(
                                                bitmap = drawableToBitmap(app.icon).asImageBitmap(),
                                                contentDescription = app.name,
                                                modifier = Modifier.size(48.dp)
                                            )

                                            Column {
                                                Text(
                                                    text = app.name,
                                                    style = MaterialTheme.typography.titleMedium
                                                )

                                                Text(
                                                    text = app.packageName,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }

//                                        Button(
//                                            onClick = {
//                                                selectedApp = app
//                                            }
//                                        ) {
//                                            Text("Lock")
//                                        }
                                    }
                                }
                            }
                        }
                    }

                    selectedApp?.let { app ->
                        AlertDialog(
                            onDismissRequest = {
                                selectedApp = null
                            },
                            title = {
                                Text("Lock App")
                            },
                            text = {
                                Text("Lock ${app.name} for 30 minutes?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        AppLockManager.lockApp(
                                            this@MainActivity,
                                            app.packageName
                                        )
                                        selectedApp = null
                                    }
                                ) {
                                    Text("Lock Now")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = {
                                        selectedApp = null
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
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

}

