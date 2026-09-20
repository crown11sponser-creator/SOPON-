package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.PlanEntity
import com.example.data.model.UserPlanEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.components.PlanCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlansScreen(
    catalogPlans: List<PlanEntity>,
    userPlans: List<UserPlanEntity>,
    settings: AppSettingsEntity?,
    onActivatePlan: (Long) -> Unit,
    onClaimPlanReward: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Available Plans, 1: My Plan History
    var historyFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, COMPLETED
    var planToActivate by remember { mutableStateOf<PlanEntity?>(null) }

    val filteredUserPlans = when (historyFilter) {
        "ACTIVE" -> userPlans.filter { it.status == "ACTIVE" }
        "COMPLETED" -> userPlans.filter { it.status == "COMPLETED" }
        else -> userPlans
    }

    val currency = settings?.currencySymbol ?: "₹"

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("plans_screen")
    ) {
        // Top Demo Warning
        DemoWarningBanner(compact = true, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldDark
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Available Plans (${catalogPlans.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "My Plans (${userPlans.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        if (selectedTab == 0) {
            // Available Plans Catalog
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(catalogPlans) { plan ->
                    PlanCard(
                        plan = plan,
                        currencySymbol = currency,
                        onActivateClick = { planToActivate = plan }
                    )
                }
            }
        } else {
            // My Plan History
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = historyFilter == "ALL",
                        onClick = { historyFilter = "ALL" },
                        label = { Text("All (${userPlans.size})") }
                    )
                    FilterChip(
                        selected = historyFilter == "ACTIVE",
                        onClick = { historyFilter = "ACTIVE" },
                        label = { Text("Active (${userPlans.count { it.status == "ACTIVE" }})") }
                    )
                    FilterChip(
                        selected = historyFilter == "COMPLETED",
                        onClick = { historyFilter = "COMPLETED" },
                        label = { Text("Completed (${userPlans.count { it.status == "COMPLETED" }})") }
                    )
                }

                if (filteredUserPlans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No plans found under this filter",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredUserPlans) { up ->
                            UserPlanHistoryCard(
                                userPlan = up,
                                currency = currency,
                                onClaimReward = { onClaimPlanReward(up.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Plan Activation Confirmation Dialog
    planToActivate?.let { plan ->
        AlertDialog(
            onDismissRequest = { planToActivate = null },
            title = {
                Text(
                    text = "Activate Demo Plan",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Activate this demo plan using virtual credits?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Plan: ${plan.name}\n• Demo Price: $currency${String.format("%,.0f", plan.demoPrice)} DEMO\n• Duration: ${plan.durationDays} Days\n• Daily Simulated Reward: $currency${String.format("%,.0f", plan.dailySimulatedReward)} DEMO\n• Total Simulated Reward: $currency${String.format("%,.0f", plan.totalSimulatedReward)} DEMO",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DemoWarningBanner(compact = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onActivatePlan(plan.id)
                        planToActivate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Confirm Virtual Activation")
                }
            },
            dismissButton = {
                TextButton(onClick = { planToActivate = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserPlanHistoryCard(
    userPlan: UserPlanEntity,
    currency: String,
    onClaimReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val progressRatio = (userPlan.daysCompleted.toFloat() / userPlan.totalDays.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = userPlan.planName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Invested: $currency${String.format("%,.0f", userPlan.investedAmount)} DEMO",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    color = if (userPlan.status == "ACTIVE") EmeraldLight else BlueLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = userPlan.status,
                        color = if (userPlan.status == "ACTIVE") EmeraldDark else BlueAccent,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Activated: ${df.format(Date(userPlan.activatedAt))}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate600,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Expires: ${df.format(Date(userPlan.expiresAt))}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate600,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress: Day ${userPlan.daysCompleted} / ${userPlan.totalDays}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                )
                Text(
                    text = "Claimed: $currency${String.format("%,.0f", userPlan.rewardsClaimed)} DEMO",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (userPlan.status == "ACTIVE") EmeraldPrimary else BlueAccent,
                trackColor = Slate100
            )

            if (userPlan.status == "ACTIVE") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onClaimReward,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Simulate Day +$currency${String.format("%,.0f", userPlan.dailyReward)} Demo Claim",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
