package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.SupportTicketEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    tickets: List<SupportTicketEntity>,
    settings: AppSettingsEntity?,
    onCreateTicket: (String, String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(imageVector = Icons.Default.AddComment, contentDescription = "New Ticket", tint = EmeraldDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = EmeraldDark,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_ticket_fab")
            ) {
                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Ticket")
            }
        },
        modifier = modifier.fillMaxSize().testTag("support_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                DemoWarningBanner(compact = true)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = EmeraldDark)
                            Text(
                                text = "Demo Support Helpdesk",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Email: ${settings?.supportEmail ?: "support@demorewards.example"}\nSupport Helpline: ${settings?.supportPhone ?: "+1 (800) 555-DEMO"}\nTelegram Support: ${settings?.telegramSupportUrl ?: "https://t.me/demorewards_support"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600, lineHeight = 18.sp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "My Support Tickets (${tickets.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (tickets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No support tickets submitted yet",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
                            )
                        }
                    }
                }
            } else {
                items(tickets) { ticket ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = BlueLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = ticket.category,
                                            color = BlueAccent,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = ticket.subject,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Surface(
                                    color = when (ticket.status) {
                                        "RESOLVED" -> EmeraldLight
                                        "PENDING" -> GoldLight
                                        else -> Slate100
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = ticket.status,
                                        color = when (ticket.status) {
                                            "RESOLVED" -> EmeraldDark
                                            "PENDING" -> GoldDark
                                            else -> Slate600
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = ticket.message,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600, lineHeight = 16.sp)
                            )

                            if (!ticket.adminReply.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldLight.copy(alpha = 0.5f))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Admin Support Response:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ticket.adminReply,
                                            style = MaterialTheme.typography.bodySmall.copy(color = Navy900, lineHeight = 15.sp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Created: ${df.format(Date(ticket.createdAt))}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTicketDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { subj, cat, msg ->
                onCreateTicket(subj, cat, msg)
                showCreateDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Demo Plan") }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Account", "Demo Wallet", "Demo Plan", "Referral", "Technical", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open Support Ticket", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it; error = null },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it; error = null },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4
                )

                error?.let {
                    Text(text = it, color = RedAccent, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isBlank() || message.isBlank()) {
                        error = "Please fill in both subject and message"
                        return@Button
                    }
                    onSubmit(subject.trim(), selectedCategory, message.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
            ) {
                Text("Submit Ticket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
