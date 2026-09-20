package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: UserEntity?,
    wallet: WalletEntity?,
    userPlans: List<UserPlanEntity>,
    settings: AppSettingsEntity?,
    unreadNotificationsCount: Int,
    onNavigateToPlans: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToTeam: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onAddDemoCredits: () -> Unit,
    onWithdrawClick: () -> Unit,
    onSimulateDailyReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activePlans = userPlans.filter { it.status == "ACTIVE" }
    val completedPlans = userPlans.filter { it.status == "COMPLETED" }

    val activeDailyRewardSum = activePlans.sumOf { it.dailyReward }
    val primaryActivePlan = activePlans.firstOrNull()

    var showAddCreditsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.take(1) ?: "U").uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Hello, ${user?.fullName ?: "Demo User"}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (user?.isAdmin == true) {
                                Surface(
                                    color = GoldAmber.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { onNavigateToAdmin() }
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        color = GoldDark,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Code: ${user?.referralCode ?: "USER00000"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Admin Switch Button
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("admin_panel_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = if (user?.isAdmin == true) GoldAmber else Slate600,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Notification bell with badge
                    Box {
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (unreadNotificationsCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                            ) {
                                Text(unreadNotificationsCount.toString())
                            }
                        }
                    }
                }
            }
        }

        // Demo Warning Banner (Mandatory)
        item {
            DemoWarningBanner()
        }

        // Wallet Card
        item {
            WalletCard(
                wallet = wallet,
                currencySymbol = settings?.currencySymbol ?: "₹",
                onAddCreditsClick = { showAddCreditsDialog = true },
                onWithdrawClick = onWithdrawClick
            )
        }

        // Quick Actions Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Inventory2,
                            label = "My Plans",
                            containerColor = Color(0xFFF3E8FF),
                            iconTint = Color(0xFF7E22CE),
                            onClick = onNavigateToPlans
                        )
                        QuickActionButton(
                            icon = Icons.Default.History,
                            label = "History",
                            containerColor = BlueLight,
                            iconTint = BlueAccent,
                            onClick = onNavigateToHistory
                        )
                        QuickActionButton(
                            icon = Icons.Default.CardGiftcard,
                            label = "Rewards",
                            containerColor = EmeraldLight,
                            iconTint = EmeraldDark,
                            onClick = onSimulateDailyReward
                        )
                        QuickActionButton(
                            icon = Icons.Default.ArrowOutward,
                            label = "Withdraw",
                            containerColor = GoldLight,
                            iconTint = GoldDark,
                            onClick = onWithdrawClick
                        )
                        QuickActionButton(
                            icon = Icons.Default.Share,
                            label = "Referral",
                            containerColor = Color(0xFFFFEDD5),
                            iconTint = Color(0xFFC2410C),
                            onClick = onNavigateToTeam
                        )
                        QuickActionButton(
                            icon = Icons.Default.SupportAgent,
                            label = "Support",
                            containerColor = Slate100,
                            iconTint = Slate600,
                            onClick = onNavigateToSupport
                        )
                    }
                }
            }
        }

        // Daily Reward Simulation Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.testTag("daily_reward_simulation_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoGraph,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Daily Reward Simulation",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Next simulated reward: Tomorrow",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            color = EmeraldLight,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Active Yield",
                                color = EmeraldDark,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Today's Demo Reward",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "${settings?.currencySymbol ?: "₹"}${String.format("%,.0f", if (activeDailyRewardSum > 0) activeDailyRewardSum else 20.0)} DEMO",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldDark
                                )
                            )
                        }

                        Button(
                            onClick = onSimulateDailyReward,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("claim_daily_reward_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Claim Simulated Reward",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar for active plan
                    val days = primaryActivePlan?.daysCompleted ?: 12
                    val totalDays = primaryActivePlan?.totalDays ?: 30
                    val progressRatio = (days.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Plan Cycle Progress: ${primaryActivePlan?.planName ?: "Starter Solar Plan"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "Day $days / $totalDays",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Simulated rewards are for demonstration only and cannot be converted to fiat currency or cash.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }

        // Statistics Grid
        item {
            Text(
                text = "Overview Statistics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Active Plans",
                    value = activePlans.size.toString(),
                    subtitle = "Running cycles",
                    icon = Icons.Default.Inventory2,
                    iconBgColor = EmeraldLight,
                    iconTint = EmeraldDark,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed Plans",
                    value = completedPlans.size.toString(),
                    subtitle = "Matured demo plans",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = BlueLight,
                    iconTint = BlueAccent,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Rewards",
                    value = "${settings?.currencySymbol ?: "₹"}${String.format("%,.0f", wallet?.totalRewards ?: 0.0)}",
                    subtitle = "Simulated yield",
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = GoldLight,
                    iconTint = GoldDark,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Referral Rewards",
                    value = "${settings?.currencySymbol ?: "₹"}${String.format("%,.0f", wallet?.referralRewards ?: 0.0)}",
                    subtitle = "Team demo bonus",
                    icon = Icons.Default.GroupAdd,
                    iconBgColor = Color(0xFFFFEDD5),
                    iconTint = Color(0xFFC2410C),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Telegram Channel Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0088CC).copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF0088CC), Color(0xFF229ED9)))
                ),
                modifier = Modifier.testTag("telegram_banner_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0088CC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Telegram",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Join Our Telegram Channel",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Get live announcements and demo updates",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val url = settings?.telegramChannelUrl ?: "https://t.me/demorewards_official"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("join_telegram_btn")
                    ) {
                        Text(
                            text = "JOIN TELEGRAM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    if (showAddCreditsDialog) {
        AlertDialog(
            onDismissRequest = { showAddCreditsDialog = false },
            title = {
                Text(
                    text = "Add Demo Credits",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Add ₹1,000 DEMO virtual credits to your balance for testing purposes. No real money or payment is involved.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DemoWarningBanner(compact = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddDemoCredits()
                        showAddCreditsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Add ₹1,000 DEMO")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCreditsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
