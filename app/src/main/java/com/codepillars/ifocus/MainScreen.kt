package com.codepillars.ifocus

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onAppLockClick: () -> Unit,
    onAppUsageClick: () -> Unit,
    onAppLimitClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "iFocus Permissions",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Allow permissions to detect, block apps, and track usage.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Accessibility Permission")
                    }

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Usage Access")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Overlay Permission")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "iFocus Tools",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Button(
                        onClick = onAppLockClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("App Lock")
                    }

                    Button(
                        onClick = onAppUsageClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("App Usage")
                    }

                    Button(
                        onClick = onAppLimitClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("App Limit")
                    }
                }
            }
        }
    }
}