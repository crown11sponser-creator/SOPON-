package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.AppSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WalletEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.components.TransactionRow
import com.example.ui.components.WalletCard
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    wallet: WalletEntity?,
    transactions: List<TransactionEntity>,
    settings: AppSettingsEntity?,
    onAddDemoCredits: () -> Unit,
    onRequestWithdrawal: (Double, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedTxnFilter by remember { mutableStateOf("ALL") }

    val currency = settings?.currencySymbol ?: "₹"

    val filteredTransactions = when (selectedTxnFilter) {
        "CREDIT" -> transactions.filter { it.type in listOf("DEMO_CREDIT", "DEMO_REWARD", "DEMO_REFERRAL", "DEMO_REFUND") }
        "DEBIT" -> transactions.filter { it.type in listOf("DEMO_PLAN", "DEMO_WITHDRAWAL") }
        "WITHDRAWAL" -> transactions.filter { it.type == "DEMO_WITHDRAWAL" }
        else -> transactions
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoWarningBanner()
        }

        item {
            WalletCard(
                wallet = wallet,
                currencySymbol = currency,
                onAddCreditsClick = onAddDemoCredits,
                onWithdrawClick = { showWithdrawDialog = true }
            )
        }

        // Wallet Balance Breakdown Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currency${String.format("%,.2f", wallet?.availableBalance ?: 0.0)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldDark)
                        )
                        Text(
                            text = "Virtual Credits",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate400)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pending Withdrawals",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currency${String.format("%,.2f", wallet?.pendingBalance ?: 0.0)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoldDark)
                        )
                        Text(
                            text = "Under Demo Review",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate400)
                        )
                    }
                }
            }
        }

        // Transactions Header & Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${filteredTransactions.size} Records",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTxnFilter == "ALL",
                    onClick = { selectedTxnFilter = "ALL" },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedTxnFilter == "CREDIT",
                    onClick = { selectedTxnFilter = "CREDIT" },
                    label = { Text("Credits") }
                )
                FilterChip(
                    selected = selectedTxnFilter == "DEBIT",
                    onClick = { selectedTxnFilter = "DEBIT" },
                    label = { Text("Debits") }
                )
                FilterChip(
                    selected = selectedTxnFilter == "WITHDRAWAL",
                    onClick = { selectedTxnFilter = "WITHDRAWAL" },
                    label = { Text("Withdrawals") }
                )
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No transactions found in this category",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions) { txn ->
                TransactionRow(txn = txn, currencySymbol = currency)
            }
        }
    }

    // Demo Withdrawal Dialog
    if (showWithdrawDialog) {
        DemoWithdrawalDialog(
            availableBalance = wallet?.availableBalance ?: 0.0,
            currency = currency,
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { amount, method, details ->
                onRequestWithdrawal(amount, method, details)
                showWithdrawDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoWithdrawalDialog(
    availableBalance: Double,
    currency: String,
    onDismiss: () -> Unit,
    onSubmit: (Double, String, String) -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("UPI DEMO") }
    var accountDetailsInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val methods = listOf("UPI DEMO", "Bank DEMO", "Wallet DEMO")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Simulated Demo Withdrawal",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Request a virtual withdrawal from your demo balance. No real money or banking API is connected.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                )

                DemoWarningBanner(compact = true)

                Text(
                    text = "Available: $currency${String.format("%,.2f", availableBalance)} DEMO",
                    style = MaterialTheme.typography.labelMedium.copy(color = EmeraldDark, fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it; errorMessage = null },
                    label = { Text("Demo Amount ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input"),
                    singleLine = true
                )

                Text(
                    text = "Select Demo Method:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = accountDetailsInput,
                    onValueChange = { accountDetailsInput = it; errorMessage = null },
                    label = {
                        Text(
                            when (selectedMethod) {
                                "UPI DEMO" -> "Demo UPI ID (e.g. user@demoupi)"
                                "Bank DEMO" -> "Demo Account & IFSC"
                                else -> "Demo Wallet Mobile / ID"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_account_input"),
                    singleLine = true
                )

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = RedAccent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountInput.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMessage = "Please enter a valid amount"
                        return@Button
                    }
                    if (amt < 100) {
                        errorMessage = "Minimum demo withdrawal is 100 DEMO"
                        return@Button
                    }
                    if (amt > availableBalance) {
                        errorMessage = "Insufficient demo balance"
                        return@Button
                    }
                    if (accountDetailsInput.isBlank()) {
                        errorMessage = "Please enter demo account details"
                        return@Button
                    }
                    onSubmit(amt, selectedMethod, accountDetailsInput.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier.testTag("submit_withdraw_btn")
            ) {
                Text("Submit Demo Withdrawal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
