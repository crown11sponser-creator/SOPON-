package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavigationTab
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DemoRewardsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: DemoRewardsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DemoRewardsApp(viewModel = viewModel)
            }
        }
    }
}

// Kept for screenshot test backward-compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun DemoRewardsApp(viewModel: DemoRewardsViewModel) {
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentWallet by viewModel.currentWallet.collectAsStateWithLifecycle()
    val userPlans by viewModel.userPlans.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val userTickets by viewModel.userTickets.collectAsStateWithLifecycle()
    val referredUsers by viewModel.referredUsers.collectAsStateWithLifecycle()
    val catalogPlans by viewModel.activeCatalogPlans.collectAsStateWithLifecycle()
    val allCatalogPlans by viewModel.allCatalogPlans.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    // Admin flows
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsStateWithLifecycle()
    val allTickets by viewModel.allTickets.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf<NavigationTab>(NavigationTab.Home) }
    var showingAdmin by remember { mutableStateOf(false) }
    var showingNotifications by remember { mutableStateOf(false) }
    var showingSupport by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (currentUserId == null) {
        AuthScreen(
            onLogin = { id, pass -> viewModel.login(id, pass) },
            onRegister = { name, mob, email, pass, ref ->
                viewModel.register(name, mob, email, pass, ref)
            },
            onQuickDemoUser = {
                viewModel.switchUser(2L) // Alex Mercer
            },
            onQuickAdmin = {
                viewModel.switchUser(1L) // Demo Admin
                showingAdmin = true
            }
        )
    } else {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = EmeraldDark,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            bottomBar = {
                if (!showingAdmin && !showingNotifications && !showingSupport) {
                    BottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    showingAdmin -> {
                        BackHandler { showingAdmin = false }
                        AdminScreen(
                            allUsers = allUsers,
                            allPlans = allCatalogPlans,
                            allTransactions = allTransactions,
                            pendingWithdrawals = pendingWithdrawals,
                            allTickets = allTickets,
                            auditLogs = auditLogs,
                            settings = appSettings,
                            onProcessWithdrawal = { txn, approved ->
                                viewModel.adminProcessWithdrawal(txn, approved)
                            },
                            onAddCreditsToUser = { uid, amt, reason ->
                                viewModel.adminAddDemoCredits(uid, amt, reason)
                            },
                            onToggleSuspendUser = { user ->
                                viewModel.adminToggleUserSuspension(user)
                            },
                            onResetPassword = { user, pass ->
                                viewModel.adminResetPassword(user, pass)
                            },
                            onSavePlan = { plan ->
                                viewModel.adminSavePlan(plan)
                            },
                            onDeletePlan = { pid ->
                                viewModel.adminDeletePlan(pid)
                            },
                            onBroadcastNotification = { title, msg, uid ->
                                viewModel.adminBroadcastNotification(title, msg, uid)
                            },
                            onReplyTicket = { tid, reply, status ->
                                viewModel.adminReplyTicket(tid, reply, status)
                            },
                            onSaveSettings = { set ->
                                viewModel.adminSaveSettings(set)
                            },
                            onBackClick = { showingAdmin = false }
                        )
                    }

                    showingNotifications -> {
                        BackHandler { showingNotifications = false }
                        NotificationsScreen(
                            notifications = notifications,
                            onMarkRead = { viewModel.markNotificationRead(it) },
                            onDelete = { viewModel.deleteNotification(it) },
                            onBackClick = { showingNotifications = false }
                        )
                    }

                    showingSupport -> {
                        BackHandler { showingSupport = false }
                        SupportScreen(
                            tickets = userTickets,
                            settings = appSettings,
                            onCreateTicket = { subj, cat, msg ->
                                viewModel.createSupportTicket(subj, cat, msg)
                            },
                            onBackClick = { showingSupport = false }
                        )
                    }

                    else -> {
                        when (selectedTab) {
                            NavigationTab.Home -> {
                                HomeScreen(
                                    user = currentUser,
                                    wallet = currentWallet,
                                    userPlans = userPlans,
                                    settings = appSettings,
                                    unreadNotificationsCount = notifications.count { !it.isRead },
                                    onNavigateToPlans = { selectedTab = NavigationTab.Plans },
                                    onNavigateToHistory = { selectedTab = NavigationTab.Plans },
                                    onNavigateToWallet = { selectedTab = NavigationTab.Wallet },
                                    onNavigateToTeam = { selectedTab = NavigationTab.Team },
                                    onNavigateToSupport = { showingSupport = true },
                                    onNavigateToNotifications = { showingNotifications = true },
                                    onNavigateToAdmin = { showingAdmin = true },
                                    onAddDemoCredits = { viewModel.addDemoCreditsSelf(1000.0) },
                                    onWithdrawClick = { selectedTab = NavigationTab.Wallet },
                                    onSimulateDailyReward = { viewModel.simulateDailyPulseForUser() }
                                )
                            }

                            NavigationTab.Plans -> {
                                PlansScreen(
                                    catalogPlans = catalogPlans,
                                    userPlans = userPlans,
                                    settings = appSettings,
                                    onActivatePlan = { viewModel.activatePlan(it) },
                                    onClaimPlanReward = { viewModel.claimIndividualPlanReward(it) }
                                )
                            }

                            NavigationTab.Team -> {
                                TeamScreen(
                                    user = currentUser,
                                    wallet = currentWallet,
                                    referredUsers = referredUsers,
                                    settings = appSettings
                                )
                            }

                            NavigationTab.Wallet -> {
                                WalletScreen(
                                    wallet = currentWallet,
                                    transactions = userTransactions,
                                    settings = appSettings,
                                    onAddDemoCredits = { viewModel.addDemoCreditsSelf(1000.0) },
                                    onRequestWithdrawal = { amt, method, details ->
                                        viewModel.requestDemoWithdrawal(amt, method, details)
                                    }
                                )
                            }

                            NavigationTab.Profile -> {
                                ProfileScreen(
                                    user = currentUser,
                                    settings = appSettings,
                                    onNavigateToAdmin = { showingAdmin = true },
                                    onNavigateToSupport = { showingSupport = true },
                                    onNavigateToNotifications = { showingNotifications = true },
                                    onLogout = { viewModel.logout() },
                                    onSwitchUser = { viewModel.switchUser(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
