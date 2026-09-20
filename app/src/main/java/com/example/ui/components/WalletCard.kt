package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletEntity
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900

@Composable
fun WalletCard(
    wallet: WalletEntity?,
    currencySymbol: String = "₹",
    onAddCreditsClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val available = wallet?.availableBalance ?: 0.0
    val totalRewards = wallet?.totalRewards ?: 0.0
    val totalInvestment = wallet?.totalInvestment ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wallet_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Navy900, Navy800, Color(0xFF1E3A5F))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Demo Balance",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "DEMO CREDITS ONLY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    // Demo simulation tag
                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Virtual Simulated",
                            color = EmeraldPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Large Balance Display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$currencySymbol${String.format("%,.2f", available)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                    )
                    Text(
                        text = "DEMO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldAmber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Demo Rewards",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "+$currencySymbol${String.format("%,.1f", totalRewards)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    Column {
                        Text(
                            text = "Total Demo Invested",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "$currencySymbol${String.format("%,.1f", totalInvestment)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    Column {
                        Text(
                            text = "Pending Demo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "$currencySymbol${String.format("%,.1f", wallet?.pendingBalance ?: 0.0)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GoldAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onAddCreditsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("add_credits_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Demo Credits",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onWithdrawClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("withdraw_demo_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.2f)))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Withdraw Demo",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
