package com.codepillars.ifocus

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onAppLockClick: () -> Unit,
    onAppUsageClick: () -> Unit,
    onAppLimitClick: () -> Unit
) {
    val context = LocalContext.current

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF070B14),
            Color(0xFF10111A),
            Color(0xFF070B14)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "iFocus",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White
        )

        Text(
            text = "Stay focused. Detect, block and track app usage easily.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFB8BAC6)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            SectionCard(
                title = "Permissions",
                subtitle = "Enable required permissions",
                icon = painterResource(R.drawable.baseline_verified_user_24),
                accent = Color(0xFF5BE584)
            ) {
                MenuItem(
                    title = "Accessibility Permission",
                    subtitle = "Detect and block distracting apps",
                    icon = painterResource(R.drawable.baseline_accessibility_new_24),
                    accent = Color(0xFF5BE584)
                ) {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }

                MenuItem(
                    title = "Usage Access",
                    subtitle = "Track app usage and screen time",
                    icon = painterResource(R.drawable.sharp_android_cell_4_bar_24),
                    accent = Color(0xFF5BE584)
                ) {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }

                MenuItem(
                    title = "Overlay Permission",
                    subtitle = "Show lock screen over other apps",
                    icon = painterResource(R.drawable.baseline_layers_24),
                    accent = Color(0xFF5BE584)
                ) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(
                title = "Tools",
                subtitle = "Manage focus and app limits",
                icon = painterResource(R.drawable.baseline_build_24),
                accent = Color(0xFFB56CFF)
            ) {
                MenuItem(
                    title = "App Lock",
                    subtitle = "Lock selected distracting apps",
                    icon = painterResource(R.drawable.baseline_lock_24),
                    accent = Color(0xFFB56CFF),
                    onClick = onAppLockClick
                )

                MenuItem(
                    title = "App Usage",
                    subtitle = "View detailed usage history",
                    icon = painterResource(R.drawable.baseline_watch_later_24),
                    accent = Color(0xFFB56CFF),
                    onClick = onAppUsageClick
                )

                MenuItem(
                    title = "App Limit",
                    subtitle = "Set daily app time limits",
                    icon = painterResource(R.drawable.baseline_hourglass_top_24),
                    accent = Color(0xFFB56CFF),
                    onClick = onAppLimitClick
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    subtitle: String,
    icon: Painter,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1B26)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = accent.copy(alpha = 0.18f)
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.padding(14.dp).size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB8BAC6)
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    subtitle: String,
    icon: Painter,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accent.copy(alpha = 0.22f)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.padding(12.dp).size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    color = Color(0xFFB8BAC6),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFB8BAC6)
            )
        }
    }
}