package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onMarkRead: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize().testTag("notifications_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DemoWarningBanner(compact = true, modifier = Modifier.padding(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No notifications yet",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else Slate100
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (notif.type) {
                                                    "REWARD" -> EmeraldLight
                                                    "PLAN" -> Color(0xFFF3E8FF)
                                                    "REFERRAL" -> Color(0xFFFFEDD5)
                                                    "WITHDRAWAL" -> GoldLight
                                                    else -> BlueLight
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (notif.type) {
                                                "REWARD" -> Icons.Default.AutoGraph
                                                "PLAN" -> Icons.Default.Inventory2
                                                "REFERRAL" -> Icons.Default.GroupAdd
                                                "WITHDRAWAL" -> Icons.Default.ArrowOutward
                                                else -> Icons.Default.Campaign
                                            },
                                            contentDescription = null,
                                            tint = when (notif.type) {
                                                "REWARD" -> EmeraldDark
                                                "PLAN" -> Color(0xFF7E22CE)
                                                "REFERRAL" -> Color(0xFFC2410C)
                                                "WITHDRAWAL" -> GoldDark
                                                else -> BlueAccent
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = notif.message,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 15.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = df.format(Date(notif.createdAt)),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Slate400,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Row {
                                    if (!notif.isRead) {
                                        IconButton(
                                            onClick = { onMarkRead(notif.id) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Mark read",
                                                tint = EmeraldDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { onDelete(notif.id) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = Slate400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
