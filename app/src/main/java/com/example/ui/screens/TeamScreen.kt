package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.UserEntity
import com.example.data.model.WalletEntity
import com.example.ui.components.DemoWarningBanner
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeamScreen(
    user: UserEntity?,
    wallet: WalletEntity?,
    referredUsers: List<UserEntity>,
    settings: AppSettingsEntity?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currency = settings?.currencySymbol ?: "₹"
    val referralCode = user?.referralCode ?: "USER48291"
    val referralLink = "https://demorewards.app/register?ref=$referralCode"

    val totalMembers = referredUsers.size
    val activeMembers = referredUsers.count { !it.isSuspended }

    val level1Count = referredUsers.size
    val level2Count = if (referredUsers.isNotEmpty()) (referredUsers.size * 2) else 0
    val level3Count = if (referredUsers.isNotEmpty()) (referredUsers.size * 3) else 0

    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("team_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoWarningBanner()
        }

        // Referral Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFEDD5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Referral & Invite Program",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Earn virtual demo credits by sharing your code",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Referral Code Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate50)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "My Referral Code",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 10.sp)
                            )
                            Text(
                                text = referralCode,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = Navy900
                                )
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", referralCode))
                                Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("copy_ref_code_btn")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Code", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Referral Link Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate50)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = referralLink,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Referral Link", referralLink))
                                Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_ref_link_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = "Copy link", tint = EmeraldDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Referral Tier Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TierPill(
                            level = "Level 1",
                            reward = "$currency${String.format("%,.0f", settings?.level1Reward ?: 10.0)} DEMO",
                            members = "$level1Count",
                            modifier = Modifier.weight(1f)
                        )
                        TierPill(
                            level = "Level 2",
                            reward = "$currency${String.format("%,.0f", settings?.level2Reward ?: 5.0)} DEMO",
                            members = "$level2Count",
                            modifier = Modifier.weight(1f)
                        )
                        TierPill(
                            level = "Level 3",
                            reward = "$currency${String.format("%,.0f", settings?.level3Reward ?: 2.0)} DEMO",
                            members = "$level3Count",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Team Overview Cards
        item {
            Text(
                text = "My Team Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Members", style = MaterialTheme.typography.labelSmall.copy(color = Slate600))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (level1Count + level2Count + level3Count).toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Demo Rewards Earned", style = MaterialTheme.typography.labelSmall.copy(color = Slate600))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currency${String.format("%,.0f", wallet?.referralRewards ?: 0.0)} DEMO",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = EmeraldDark)
                        )
                    }
                }
            }
        }

        // Team Member List
        item {
            Text(
                text = "Referred Members List (${referredUsers.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (referredUsers.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No team members yet. Share your referral code to invite others!",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                        )
                    }
                }
            }
        } else {
            items(referredUsers) { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.fullName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark)
                                )
                            }
                            Column {
                                Text(
                                    text = member.fullName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                )
                                Text(
                                    text = "Joined ${df.format(Date(member.registeredAt))}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 11.sp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                color = if (member.isSuspended) RedLight else EmeraldLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (member.isSuspended) "Inactive" else "Active",
                                    color = if (member.isSuspended) RedAccent else EmeraldDark,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Yield: +$currency${String.format("%,.0f", settings?.level1Reward ?: 25.0)} DEMO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldDark, fontSize = 10.5.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TierPill(
    level: String,
    reward: String,
    members: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Slate100)
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = level, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
            Text(text = reward, style = MaterialTheme.typography.bodySmall.copy(color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 10.sp))
            Text(text = "$members users", style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 9.sp))
        }
    }
}
