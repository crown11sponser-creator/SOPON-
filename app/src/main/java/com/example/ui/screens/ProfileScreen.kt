package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.UserEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    user: UserEntity?,
    settings: AppSettingsEntity?,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    onSwitchUser: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showChangePassDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English (Default)") }

    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoWarningBanner(compact = true)
        }

        // Profile Avatar & Info Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.take(1) ?: "U").uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user?.fullName ?: "Demo User",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = user?.email ?: "demo@example.com",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = if (user?.isAdmin == true) GoldLight else EmeraldLight,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = if (user?.isAdmin == true) "Administrator Account" else "Standard Demo Account",
                            color = if (user?.isAdmin == true) GoldDark else EmeraldDark,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = CardBorder)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detail rows
                    DetailRow(label = "Mobile", value = user?.mobile ?: "Not provided")
                    DetailRow(label = "Referral Code", value = user?.referralCode ?: "USER00000")
                    DetailRow(label = "Registered Since", value = df.format(Date(user?.registeredAt ?: System.currentTimeMillis())))
                }
            }
        }

        // Admin Portal Quick Action (Always accessible for testing)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                modifier = Modifier.clickable { onNavigateToAdmin() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.Black)
                        }
                        Column {
                            Text(
                                text = "Admin Management Console",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "Manage users, plans, withdrawals & settings",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }
        }

        // Account & Security Settings
        item {
            Text(
                text = "Preferences & Security",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update simulated account credentials",
                        onClick = { showChangePassDialog = true }
                    )
                    Divider(color = Slate100)
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = if (notificationsEnabled) "Enabled" else "Muted",
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                    )
                    Divider(color = Slate100)
                    SettingsRow(
                        icon = Icons.Default.Translate,
                        title = "Language",
                        subtitle = selectedLanguage,
                        onClick = {
                            selectedLanguage = if (selectedLanguage.startsWith("English")) "Hindi (हिंदी)" else "English (Default)"
                        }
                    )
                    Divider(color = Slate100)
                    SettingsRow(
                        icon = Icons.Default.SupportAgent,
                        title = "Customer Support",
                        subtitle = "View or open tickets",
                        onClick = onNavigateToSupport
                    )
                }
            }
        }

        // Logout / Switch User
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = RedLight, contentColor = RedAccent),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Account", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showChangePassDialog) {
        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Change Password", fontWeight = FontWeight.Bold) },
            text = {
                Text("In demo mode, passwords can be reset directly or via administrator account.")
            },
            confirmButton = {
                Button(
                    onClick = { showChangePassDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Slate600, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp))
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
        }
    }
}
