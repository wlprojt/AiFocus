package com.codepillars.ifocus


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppLimitScreen(
    navController: NavController,
    apps: List<AppInfo>,
    drawableToBitmap: (Drawable) -> Bitmap
) {
    val context = LocalContext.current

    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var limitMinutes by remember { mutableStateOf("") }
    var refreshKey by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {navController.popBackStack()},
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "back"
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Set App Usage Limit",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(apps) { app ->
                val currentLimit = remember(refreshKey) {
                    AppLockManager.getDailyLimit(
                        context,
                        app.packageName
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
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
                                    text = if (currentLimit > 0) {
                                        "Limit: $currentLimit minutes/day"
                                    } else {
                                        "No limit set"
                                    },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                selectedApp = app
                                limitMinutes = if (currentLimit > 0) {
                                    currentLimit.toString()
                                } else {
                                    ""
                                }
                            }
                        ) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }

    selectedApp?.let { app ->
        AlertDialog(
            onDismissRequest = {
                selectedApp = null
                limitMinutes = ""
            },
            title = {
                Text("Set Limit")
            },
            text = {
                Column {
                    Text("Daily limit for ${app.name}")

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = limitMinutes,
                        onValueChange = {
                            limitMinutes = it.filter { char -> char.isDigit() }
                        },
                        label = {
                            Text("Minutes")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val minutes = limitMinutes.toIntOrNull()

                        if (minutes != null && minutes > 0) {
                            AppLockManager.setDailyLimit(
                                context,
                                app.packageName,
                                minutes
                            )

                            refreshKey++
                        }

                        selectedApp = null
                        limitMinutes = ""
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        selectedApp = null
                        limitMinutes = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}