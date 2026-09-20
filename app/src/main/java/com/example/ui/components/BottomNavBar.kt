package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary

sealed class NavigationTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavigationTab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Plans : NavigationTab("plans", "Plans", Icons.Filled.Inventory2, Icons.Outlined.Inventory2)
    object Team : NavigationTab("team", "Team", Icons.Filled.Groups, Icons.Outlined.Groups)
    object Wallet : NavigationTab("wallet", "Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Profile : NavigationTab("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NavigationTab.Home,
        NavigationTab.Plans,
        NavigationTab.Team,
        NavigationTab.Wallet,
        NavigationTab.Profile
    )

    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab.route == tab.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldDark,
                    selectedTextColor = EmeraldDark,
                    indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.route}")
            )
        }
    }
}
