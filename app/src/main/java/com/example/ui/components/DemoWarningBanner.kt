package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@Composable
fun DemoWarningBanner(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GoldLight.copy(alpha = 0.95f))
            .border(1.dp, GoldAmber, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = if (compact) 8.dp else 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Demo Mode Warning",
                tint = GoldDark,
                modifier = Modifier.size(if (compact) 18.dp else 22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DEMO MODE – NO REAL MONEY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = GoldDark,
                        letterSpacing = 0.5.sp
                    )
                )
                if (!compact) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "All balances, rewards, investments and withdrawals shown in this application are simulated and have no real monetary value.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF78350F),
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }
    }
}
