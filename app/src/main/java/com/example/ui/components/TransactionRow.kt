package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionRow(
    txn: TransactionEntity,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier
) {
    val isCredit = txn.type in listOf("DEMO_CREDIT", "DEMO_REWARD", "DEMO_REFERRAL", "DEMO_REFUND")
    val icon: ImageVector = when (txn.type) {
        "DEMO_CREDIT" -> Icons.Default.AddCard
        "DEMO_PLAN" -> Icons.Default.Inventory2
        "DEMO_REWARD" -> Icons.Default.AutoGraph
        "DEMO_REFERRAL" -> Icons.Default.GroupAdd
        "DEMO_WITHDRAWAL" -> Icons.Default.ArrowOutward
        "DEMO_REFUND" -> Icons.Default.Replay
        else -> Icons.Default.ReceiptLong
    }

    val iconBgColor = when (txn.type) {
        "DEMO_CREDIT", "DEMO_REWARD" -> EmeraldPrimary.copy(alpha = 0.15f)
        "DEMO_REFERRAL" -> BlueAccent.copy(alpha = 0.15f)
        "DEMO_PLAN" -> Color(0xFFF3E8FF)
        "DEMO_WITHDRAWAL" -> GoldAmber.copy(alpha = 0.15f)
        else -> Slate100
    }

    val iconTint = when (txn.type) {
        "DEMO_CREDIT", "DEMO_REWARD" -> EmeraldDark
        "DEMO_REFERRAL" -> BlueAccent
        "DEMO_PLAN" -> Color(0xFF7E22CE)
        "DEMO_WITHDRAWAL" -> GoldDark
        else -> Slate600
    }

    val statusColor = when (txn.status) {
        "COMPLETED" -> EmeraldDark
        "PENDING" -> GoldDark
        "REJECTED" -> RedAccent
        else -> Slate600
    }

    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(txn.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("txn_row_${txn.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = txn.type,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = txn.type.replace("_", " "),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Surface(
                                color = GoldAmber.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Demo",
                                    color = GoldDark,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = (if (isCredit) "+" else "-") + "$currencySymbol${String.format("%,.2f", txn.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isCredit) EmeraldDark else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = txn.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = txn.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${txn.transactionUid}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Demo Transaction",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
