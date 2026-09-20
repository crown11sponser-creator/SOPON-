package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.DemoWarningBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    allUsers: List<UserEntity>,
    allPlans: List<PlanEntity>,
    allTransactions: List<TransactionEntity>,
    pendingWithdrawals: List<TransactionEntity>,
    allTickets: List<SupportTicketEntity>,
    auditLogs: List<AuditLogEntity>,
    settings: AppSettingsEntity?,
    onProcessWithdrawal: (TransactionEntity, Boolean) -> Unit,
    onAddCreditsToUser: (Long, Double, String) -> Unit,
    onToggleSuspendUser: (UserEntity) -> Unit,
    onResetPassword: (UserEntity, String) -> Unit,
    onSavePlan: (PlanEntity) -> Unit,
    onDeletePlan: (Long) -> Unit,
    onBroadcastNotification: (String, String, Long?) -> Unit,
    onReplyTicket: (Long, String, String) -> Unit,
    onSaveSettings: (AppSettingsEntity) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAdminTab by remember { mutableIntStateOf(0) }
    // 0: Overview, 1: Users, 2: Plans, 3: Withdrawals, 4: Support, 5: Broadcast, 6: Settings & Logs

    val tabs = listOf("Overview", "Users", "Plans", "Withdrawals", "Support", "Broadcast", "Settings & Logs")
    val currency = settings?.currencySymbol ?: "₹"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldDark)
                        Text("Admin Control Console", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit Admin")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize().testTag("admin_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DemoWarningBanner(compact = true, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldDark,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedAdminTab == index,
                        onClick = { selectedAdminTab = index },
                        text = {
                            Text(
                                text = if (index == 3 && pendingWithdrawals.isNotEmpty()) "$label (${pendingWithdrawals.size})" else label,
                                fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (index == 3 && pendingWithdrawals.isNotEmpty()) GoldDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            when (selectedAdminTab) {
                0 -> AdminOverviewTab(allUsers, allPlans, allTransactions, pendingWithdrawals, currency)
                1 -> AdminUsersTab(allUsers, currency, onAddCreditsToUser, onToggleSuspendUser, onResetPassword)
                2 -> AdminPlansTab(allPlans, currency, onSavePlan, onDeletePlan)
                3 -> AdminWithdrawalsTab(pendingWithdrawals, allTransactions, currency, onProcessWithdrawal)
                4 -> AdminSupportTab(allTickets, onReplyTicket)
                5 -> AdminBroadcastTab(allUsers, onBroadcastNotification)
                6 -> AdminSettingsAndLogsTab(settings, auditLogs, onSaveSettings)
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    users: List<UserEntity>,
    plans: List<PlanEntity>,
    transactions: List<TransactionEntity>,
    pendingWithdrawals: List<TransactionEntity>,
    currency: String
) {
    val totalInvestments = transactions.filter { it.type == "DEMO_PLAN" }.sumOf { it.amount }
    val totalRewards = transactions.filter { it.type in listOf("DEMO_REWARD", "DEMO_REFERRAL") }.sumOf { it.amount }
    val totalWithdrawals = transactions.filter { it.type == "DEMO_WITHDRAWAL" && it.status == "COMPLETED" }.sumOf { it.amount }
    val pendingWithdrawalsSum = pendingWithdrawals.sumOf { it.amount }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Simulation Platform Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Total Users",
                    value = users.size.toString(),
                    subtitle = "${users.count { it.isSuspended }} suspended",
                    icon = Icons.Default.People,
                    iconBgColor = BlueLight,
                    iconTint = BlueAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Catalog Plans",
                    value = plans.size.toString(),
                    subtitle = "${plans.count { it.isActive }} active in store",
                    icon = Icons.Default.Inventory2,
                    iconBgColor = EmeraldLight,
                    iconTint = EmeraldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Total Demo Invested",
                    value = "$currency${String.format("%,.0f", totalInvestments)}",
                    subtitle = "Virtual product plans",
                    icon = Icons.Default.Savings,
                    iconBgColor = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF7E22CE),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Demo Rewards Yield",
                    value = "$currency${String.format("%,.0f", totalRewards)}",
                    subtitle = "Simulated bonuses",
                    icon = Icons.Default.AutoGraph,
                    iconBgColor = EmeraldLight,
                    iconTint = EmeraldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Pending Demo Withdrawals",
                    value = "${pendingWithdrawals.size} Requests",
                    subtitle = "$currency${String.format("%,.0f", pendingWithdrawalsSum)} DEMO",
                    icon = Icons.Default.PendingActions,
                    iconBgColor = GoldLight,
                    iconTint = GoldDark,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Paid Demo Withdrawals",
                    value = "$currency${String.format("%,.0f", totalWithdrawals)}",
                    subtitle = "Approved simulations",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = BlueLight,
                    iconTint = BlueAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Admin Safety Notice", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "All operations executed in this console act exclusively on the local Room simulation engine. No funds or banking webhooks are triggered.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<UserEntity>,
    currency: String,
    onAddCredits: (Long, Double, String) -> Unit,
    onToggleSuspend: (UserEntity) -> Unit,
    onResetPassword: (UserEntity, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForCredits by remember { mutableStateOf<UserEntity?>(null) }
    var creditsAmount by remember { mutableStateOf("1000") }
    var creditsReason by remember { mutableStateOf("Testing demo credit addition") }

    val filtered = users.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true) ||
        it.referralCode.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search users by name, email, code...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                Text(user.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(user.email, style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text("Ref Code: ${user.referralCode} | Mobile: ${user.mobile}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            }

                            Surface(
                                color = if (user.isSuspended) RedLight else EmeraldLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (user.isSuspended) "SUSPENDED" else "ACTIVE",
                                    color = if (user.isSuspended) RedAccent else EmeraldDark,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { selectedUserForCredits = user },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+ Demo Credits", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onToggleSuspend(user) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (user.isSuspended) EmeraldDark else RedAccent
                                )
                            ) {
                                Text(if (user.isSuspended) "Unsuspend" else "Suspend", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedUserForCredits?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUserForCredits = null },
            title = { Text("Add Demo Credits to ${user.fullName}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Virtual demo credits will be added to the user's wallet.")
                    OutlinedTextField(
                        value = creditsAmount,
                        onValueChange = { creditsAmount = it },
                        label = { Text("Demo Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = creditsReason,
                        onValueChange = { creditsReason = it },
                        label = { Text("Reason / Note") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = creditsAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onAddCredits(user.id, amt, creditsReason)
                        }
                        selectedUserForCredits = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Add Credits")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForCredits = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminPlansTab(
    plans: List<PlanEntity>,
    currency: String,
    onSavePlan: (PlanEntity) -> Unit,
    onDeletePlan: (Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = EmeraldDark,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Demo Plan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Manage Catalog Plans (${plans.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            items(plans) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                Text(plan.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Price: $currency${String.format("%,.0f", plan.demoPrice)} | Yield: +$currency${String.format("%,.0f", plan.dailySimulatedReward)}/day", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text("Duration: ${plan.durationDays} Days | Total: $currency${String.format("%,.0f", plan.totalSimulatedReward)} DEMO", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            }

                            Switch(
                                checked = plan.isActive,
                                onCheckedChange = { onSavePlan(plan.copy(isActive = it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            IconButton(onClick = { onDeletePlan(plan.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedAccent)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlanDialog(
            currency = currency,
            onDismiss = { showCreateDialog = false },
            onSave = {
                onSavePlan(it)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreatePlanDialog(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (PlanEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("500") }
    var dailyRewardStr by remember { mutableStateOf("35") }
    var durationStr by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Demo Plan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plan Name") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, maxLines = 2)
                OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Price ($currency DEMO)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = dailyRewardStr, onValueChange = { dailyRewardStr = it }, label = { Text("Daily Demo Reward") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = durationStr, onValueChange = { durationStr = it }, label = { Text("Duration (Days)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 500.0
                    val daily = dailyRewardStr.toDoubleOrNull() ?: 35.0
                    val duration = durationStr.toIntOrNull() ?: 30
                    val total = daily * duration
                    onSave(
                        PlanEntity(
                            name = name.ifBlank { "Custom Demo Plan" },
                            description = description.ifBlank { "High yield simulated demo rewards." },
                            demoPrice = price,
                            durationDays = duration,
                            dailySimulatedReward = daily,
                            totalSimulatedReward = total,
                            productIcon = "hub"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
            ) {
                Text("Create Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminWithdrawalsTab(
    pendingWithdrawals: List<TransactionEntity>,
    allTransactions: List<TransactionEntity>,
    currency: String,
    onProcess: (TransactionEntity, Boolean) -> Unit
) {
    val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Pending Demo Withdrawals (${pendingWithdrawals.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Slate100)) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No pending demo withdrawal requests.", color = Slate600)
                    }
                }
            }
        } else {
            items(pendingWithdrawals) { txn ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("User #${txn.userId}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text("$currency${String.format("%,.2f", txn.amount)} DEMO", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = GoldDark))
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(txn.description, style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                        Text("Requested: ${df.format(Date(txn.timestamp))}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onProcess(txn, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Approve Demo", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onProcess(txn, false) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAccent),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reject & Refund", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSupportTab(
    tickets: List<SupportTicketEntity>,
    onReplyTicket: (Long, String, String) -> Unit
) {
    var selectedTicket by remember { mutableStateOf<SupportTicketEntity?>(null) }
    var replyText by remember { mutableStateOf("") }
    var statusChoice by remember { mutableStateOf("RESOLVED") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("User Support Tickets (${tickets.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        items(tickets) { t ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(t.subject, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(t.status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (t.status == "RESOLVED") EmeraldDark else GoldDark))
                    }
                    Text("Category: ${t.category} | User #${t.userId}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(t.message, style = MaterialTheme.typography.bodySmall)

                    if (!t.adminReply.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Admin Reply: ${t.adminReply}", style = MaterialTheme.typography.bodySmall.copy(color = EmeraldDark))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            selectedTicket = t
                            replyText = t.adminReply ?: ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reply / Update", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    selectedTicket?.let { ticket ->
        AlertDialog(
            onDismissRequest = { selectedTicket = null },
            title = { Text("Reply Ticket #${ticket.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = replyText, onValueChange = { replyText = it }, label = { Text("Response") }, modifier = Modifier.height(90.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = statusChoice == "RESOLVED", onClick = { statusChoice = "RESOLVED" }, label = { Text("RESOLVED") })
                        FilterChip(selected = statusChoice == "PENDING", onClick = { statusChoice = "PENDING" }, label = { Text("PENDING") })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReplyTicket(ticket.id, replyText, statusChoice)
                        selectedTicket = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Save Reply")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTicket = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminBroadcastTab(
    users: List<UserEntity>,
    onBroadcast: (String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("Simulated Rewards Update") }
    var message by remember { mutableStateOf("New demo product plans and rewards are now active.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Broadcast System Notification", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Send simulated in-app alerts to all ${users.size} platform users.", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Notification Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Notification Message") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            maxLines = 4
        )

        Button(
            onClick = {
                if (title.isNotBlank() && message.isNotBlank()) {
                    onBroadcast(title.trim(), message.trim(), null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Campaign, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Broadcast to All Users", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminSettingsAndLogsTab(
    settings: AppSettingsEntity?,
    auditLogs: List<AuditLogEntity>,
    onSaveSettings: (AppSettingsEntity) -> Unit
) {
    var appName by remember { mutableStateOf(settings?.appName ?: "Demo Rewards") }
    var currency by remember { mutableStateOf(settings?.currencySymbol ?: "₹") }
    var minWithdrawalStr by remember { mutableStateOf((settings?.minWithdrawal ?: 100.0).toString()) }
    var telegramUrl by remember { mutableStateOf(settings?.telegramChannelUrl ?: "https://t.me/demorewards_official") }
    val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("System Configuration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("App Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("Currency Symbol") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = minWithdrawalStr, onValueChange = { minWithdrawalStr = it }, label = { Text("Min Withdrawal (DEMO)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = telegramUrl, onValueChange = { telegramUrl = it }, label = { Text("Telegram Channel URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Button(
                        onClick = {
                            val cur = settings ?: AppSettingsEntity()
                            onSaveSettings(
                                cur.copy(
                                    appName = appName,
                                    currencySymbol = currency,
                                    minWithdrawal = minWithdrawalStr.toDoubleOrNull() ?: 100.0,
                                    telegramChannelUrl = telegramUrl
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Configuration")
                    }
                }
            }
        }

        item {
            Text("Admin Audit Logs (${auditLogs.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.action, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Navy900))
                        Text(df.format(Date(log.timestamp)), style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 9.sp))
                    }
                    Text("Admin: ${log.adminName}", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldDark))
                    Text(log.details, style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp))
                }
            }
        }
    }
}
