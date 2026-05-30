package com.codepillars.ifocus

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppLockScreen(
    navController: NavController,
    apps: List<AppInfo>,
    drawableToBitmap: (Drawable) -> Bitmap
) {
    val context = LocalContext.current
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    text = "Select App to Lock",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(apps) { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedApp = app
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
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

//                            Button(
//                                onClick = {
//                                    selectedApp = app
//                                }
//                            ) {
//                                Text("Lock")
//                            }
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
                                    context,
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