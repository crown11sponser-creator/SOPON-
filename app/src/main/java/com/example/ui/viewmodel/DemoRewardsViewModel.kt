package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DemoDataSeeder
import com.example.data.local.DemoRewardsDatabase
import com.example.data.model.*
import com.example.data.repository.DemoRewardsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DemoRewardsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DemoRewardsDatabase.getDatabase(application)
    private val repository = DemoRewardsRepository(db.demoRewardsDao())

    private val _currentUserId = MutableStateFlow<Long?>(2L) // default to seeded demo user Alex Mercer
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getUser(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentWallet: StateFlow<WalletEntity?> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getWallet(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userPlans: StateFlow<List<UserPlanEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getUserPlans(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getUserTransactions(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getNotifications(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTickets: StateFlow<List<SupportTicketEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getUserTickets(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referredUsers: StateFlow<List<UserEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getReferredUsers(user.referralCode) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCatalogPlans: StateFlow<List<PlanEntity>> = repository.activePlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCatalogPlans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin feeds
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWithdrawals: StateFlow<List<TransactionEntity>> = repository.pendingWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ephemeral UI states
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            DemoDataSeeder.seedInitialDataIfEmpty(db.demoRewardsDao())
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun switchUser(userId: Long?) {
        _currentUserId.value = userId
    }

    fun logout() {
        _currentUserId.value = null
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.login(identifier, pass)
            _isLoading.value = false
            res.fold(
                onSuccess = { user ->
                    _currentUserId.value = user.id
                    _snackbarMessage.value = "Welcome back, ${user.fullName}!"
                    onSuccess()
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Login failed"
                }
            )
        }
    }

    fun register(
        fullName: String,
        mobile: String,
        email: String,
        passwordPlain: String,
        referralCode: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.registerUser(fullName, mobile, email, passwordPlain, referralCode)
            _isLoading.value = false
            res.fold(
                onSuccess = { user ->
                    _currentUserId.value = user.id
                    _snackbarMessage.value = "Registration successful! Welcome bonus ₹1,500 DEMO credited."
                    onSuccess()
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Registration failed"
                }
            )
        }
    }

    fun activatePlan(planId: Long) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.activatePlan(uid, planId)
            _isLoading.value = false
            res.fold(
                onSuccess = {
                    _snackbarMessage.value = "Plan activated! Daily simulated reward cycle started."
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Failed to activate plan"
                }
            )
        }
    }

    fun simulateDailyPulseForUser() {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.simulateDailyRewards(uid)
            _isLoading.value = false
            res.fold(
                onSuccess = { amount ->
                    _snackbarMessage.value = "Simulated Daily Reward: +₹$amount DEMO added to wallet!"
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Failed to claim rewards"
                }
            )
        }
    }

    fun claimIndividualPlanReward(userPlanId: Long) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.processDailyClaimForPlan(uid, userPlanId)
            _isLoading.value = false
            res.fold(
                onSuccess = { amount ->
                    _snackbarMessage.value = "Claimed +₹$amount DEMO reward for this plan!"
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Claim failed"
                }
            )
        }
    }

    fun requestDemoWithdrawal(amount: Double, method: String, accountDetails: String, onSuccess: () -> Unit = {}) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.requestDemoWithdrawal(uid, amount, method, accountDetails)
            _isLoading.value = false
            res.fold(
                onSuccess = {
                    _snackbarMessage.value = "Demo withdrawal request created. No real money will be transferred."
                    onSuccess()
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Withdrawal request failed"
                }
            )
        }
    }

    fun addDemoCreditsSelf(amount: Double = 1000.0) {
        val uid = _currentUserId.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addDemoCreditsToUser(uid, amount, "User Self-Test", "Demo Credits Top-up (Simulation)")
            _snackbarMessage.value = "Added ₹$amount DEMO test credits to your virtual wallet!"
        }
    }

    fun createSupportTicket(subject: String, category: String, message: String, onSuccess: () -> Unit = {}) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.createSupportTicket(uid, subject, category, message)
            _isLoading.value = false
            res.fold(
                onSuccess = {
                    _snackbarMessage.value = "Support ticket submitted successfully!"
                    onSuccess()
                },
                onFailure = { err ->
                    _snackbarMessage.value = err.message ?: "Failed to submit ticket"
                }
            )
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    // Admin Operations
    fun adminAddDemoCredits(targetUserId: Long, amount: Double, reason: String) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.addDemoCreditsToUser(targetUserId, amount, admin, reason)
            _snackbarMessage.value = "Admin added ₹$amount DEMO credits to user #$targetUserId."
        }
    }

    fun adminProcessWithdrawal(txn: TransactionEntity, isApproved: Boolean) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.updateWithdrawalStatus(txn, isApproved, admin)
            _snackbarMessage.value = if (isApproved) "Demo withdrawal approved." else "Demo withdrawal rejected and refunded."
        }
    }

    fun adminSavePlan(plan: PlanEntity) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.savePlan(plan, admin)
            _snackbarMessage.value = "Plan saved successfully."
        }
    }

    fun adminDeletePlan(planId: Long) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.deletePlan(planId, admin)
            _snackbarMessage.value = "Plan deleted."
        }
    }

    fun adminToggleUserSuspension(user: UserEntity) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.toggleUserSuspension(user, admin)
            _snackbarMessage.value = "User status toggled."
        }
    }

    fun adminResetPassword(user: UserEntity, newPass: String) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.resetUserPassword(user, newPass, admin)
            _snackbarMessage.value = "User password reset."
        }
    }

    fun adminReplyTicket(ticketId: Long, reply: String, newStatus: String) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.replySupportTicket(ticketId, reply, newStatus, admin)
            _snackbarMessage.value = "Support ticket updated."
        }
    }

    fun adminSaveSettings(settings: AppSettingsEntity) {
        val admin = currentUser.value?.fullName ?: "Admin"
        viewModelScope.launch {
            repository.saveSettings(settings, admin)
            _snackbarMessage.value = "Settings updated successfully."
        }
    }

    fun adminBroadcastNotification(title: String, message: String, targetUserId: Long? = null) {
        viewModelScope.launch {
            repository.broadcastNotification(title, message, targetUserId)
            _snackbarMessage.value = "Notification sent."
        }
    }
}
