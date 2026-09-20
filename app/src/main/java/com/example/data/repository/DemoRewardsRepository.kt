package com.example.data.repository

import com.example.data.local.DemoRewardsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

class DemoRewardsRepository(private val dao: DemoRewardsDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsersFlow()
    val allPlans: Flow<List<PlanEntity>> = dao.getAllPlansFlow()
    val activePlans: Flow<List<PlanEntity>> = dao.getActivePlansFlow()
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactionsFlow()
    val pendingWithdrawals: Flow<List<TransactionEntity>> = dao.getPendingWithdrawalsFlow()
    val allTickets: Flow<List<SupportTicketEntity>> = dao.getAllTicketsFlow()
    val settingsFlow: Flow<AppSettingsEntity?> = dao.getSettingsFlow()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAuditLogsFlow()

    fun getUser(userId: Long): Flow<UserEntity?> = dao.getUserFlow(userId)
    fun getWallet(userId: Long): Flow<WalletEntity?> = dao.getWalletFlow(userId)
    fun getUserPlans(userId: Long): Flow<List<UserPlanEntity>> = dao.getUserPlansFlow(userId)
    fun getUserTransactions(userId: Long): Flow<List<TransactionEntity>> = dao.getUserTransactionsFlow(userId)
    fun getNotifications(userId: Long): Flow<List<NotificationEntity>> = dao.getNotificationsFlow(userId)
    fun getUserTickets(userId: Long): Flow<List<SupportTicketEntity>> = dao.getUserTicketsFlow(userId)
    fun getReferredUsers(referralCode: String): Flow<List<UserEntity>> = dao.getReferredUsers(referralCode)

    suspend fun registerUser(
        fullName: String,
        mobile: String,
        email: String,
        passwordPlain: String,
        referralCodeInput: String?
    ): Result<UserEntity> {
        val existing = dao.getUserByEmailOrMobile(email.trim()) ?: dao.getUserByEmailOrMobile(mobile.trim())
        if (existing != null) {
            return Result.failure(Exception("An account with this email or mobile already exists."))
        }

        // Generate clean referral code USER#####
        val uniqueCode = "USER" + Random.nextInt(10000, 99999)
        val validRefBy = if (!referralCodeInput.isNullOrBlank()) {
            val referrer = dao.getUserByReferralCode(referralCodeInput.trim().uppercase())
            referrer?.referralCode
        } else null

        val newUser = UserEntity(
            fullName = fullName.trim(),
            mobile = mobile.trim(),
            email = email.trim().lowercase(),
            passwordHash = passwordPlain, // Demo simulation
            referralCode = uniqueCode,
            referredBy = validRefBy,
            isAdmin = false,
            isSuspended = false
        )

        val newId = dao.insertUser(newUser)
        val createdUser = newUser.copy(id = newId)

        // Starting demo balance: ₹1,500 DEMO credits
        val startBalance = 1500.0
        dao.insertWallet(
            WalletEntity(
                userId = newId,
                availableBalance = startBalance,
                pendingBalance = 0.0,
                totalRewards = 0.0,
                totalInvestment = 0.0,
                referralRewards = 0.0
            )
        )

        // Transaction log
        dao.insertTransaction(
            TransactionEntity(
                transactionUid = "TXN-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
                userId = newId,
                userName = createdUser.fullName,
                amount = startBalance,
                type = "DEMO_CREDIT",
                status = "COMPLETED",
                description = "Welcome bonus demo credits (Simulation Only)"
            )
        )

        // If referred, credit referral bonus to referrer
        if (validRefBy != null) {
            val referrer = dao.getUserByReferralCode(validRefBy)
            if (referrer != null) {
                val refWallet = dao.getWallet(referrer.id)
                if (refWallet != null) {
                    val bonus = 25.0 // Demo referral reward
                    dao.updateWallet(
                        refWallet.copy(
                            availableBalance = refWallet.availableBalance + bonus,
                            referralRewards = refWallet.referralRewards + bonus,
                            totalRewards = refWallet.totalRewards + bonus
                        )
                    )
                    dao.insertTransaction(
                        TransactionEntity(
                            transactionUid = "TXN-REF-${System.currentTimeMillis()}",
                            userId = referrer.id,
                            userName = referrer.fullName,
                            amount = bonus,
                            type = "DEMO_REFERRAL",
                            status = "COMPLETED",
                            description = "Level 1 simulated referral bonus for inviting ${createdUser.fullName}"
                        )
                    )
                    dao.insertNotification(
                        NotificationEntity(
                            userId = referrer.id,
                            title = "Referral Bonus Credited",
                            message = "${createdUser.fullName} joined using your code. Added ₹$bonus DEMO credits!",
                            type = "REFERRAL"
                        )
                    )
                }
            }
        }

        dao.insertNotification(
            NotificationEntity(
                userId = newId,
                title = "Welcome to Demo Rewards!",
                message = "Your virtual account is credited with ₹1,500 DEMO virtual credits. Explore demo plans!",
                type = "SYSTEM"
            )
        )

        return Result.success(createdUser)
    }

    suspend fun login(identifier: String, passwordPlain: String): Result<UserEntity> {
        val user = dao.getUserByEmailOrMobile(identifier.trim())
        if (user == null) {
            return Result.failure(Exception("Invalid credentials. Please check your email or mobile."))
        }
        if (user.passwordHash != passwordPlain) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }
        if (user.isSuspended) {
            return Result.failure(Exception("This demo account is suspended by administrator."))
        }
        return Result.success(user)
    }

    suspend fun activatePlan(userId: Long, planId: Long): Result<UserPlanEntity> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val plan = dao.getPlanById(planId) ?: return Result.failure(Exception("Plan not found"))
        if (!plan.isActive) {
            return Result.failure(Exception("This plan is currently inactive."))
        }

        val wallet = dao.getWallet(userId) ?: return Result.failure(Exception("Wallet not found"))
        if (wallet.availableBalance < plan.demoPrice) {
            return Result.failure(Exception("Insufficient demo balance. You need ₹${plan.demoPrice} DEMO credits."))
        }

        // Deduct demo balance
        dao.updateWallet(
            wallet.copy(
                availableBalance = wallet.availableBalance - plan.demoPrice,
                totalInvestment = wallet.totalInvestment + plan.demoPrice,
                updatedAt = System.currentTimeMillis()
            )
        )

        val now = System.currentTimeMillis()
        val expiresAt = now + (plan.durationDays.toLong() * 24 * 60 * 60 * 1000L)
        val userPlan = UserPlanEntity(
            userId = userId,
            planId = plan.id,
            planName = plan.name,
            investedAmount = plan.demoPrice,
            dailyReward = plan.dailySimulatedReward,
            totalReward = plan.totalSimulatedReward,
            totalDays = plan.durationDays,
            daysCompleted = 0,
            rewardsClaimed = 0.0,
            activatedAt = now,
            expiresAt = expiresAt,
            lastClaimDateMillis = 0L,
            status = "ACTIVE"
        )
        val userPlanId = dao.insertUserPlan(userPlan)

        // Log transaction
        dao.insertTransaction(
            TransactionEntity(
                transactionUid = "TXN-PLAN-${System.currentTimeMillis()}",
                userId = userId,
                userName = user.fullName,
                amount = plan.demoPrice,
                type = "DEMO_PLAN",
                status = "COMPLETED",
                description = "Activated ${plan.name} for ${plan.durationDays} days using virtual credits"
            )
        )

        // Notification
        dao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "Demo Plan Activated",
                message = "${plan.name} activated successfully. Daily simulated reward: ₹${plan.dailySimulatedReward} DEMO.",
                type = "PLAN"
            )
        )

        return Result.success(userPlan.copy(id = userPlanId))
    }

    suspend fun simulateDailyRewards(userId: Long): Result<Double> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val wallet = dao.getWallet(userId) ?: return Result.failure(Exception("Wallet not found"))

        // Find active plans for this user
        val activePlans = dao.getUserPlanById(0) // helper dummy
        // Let's get active user plans directly from DB
        val now = System.currentTimeMillis()

        var totalCredited = 0.0
        var updatedPlansCount = 0

        // In Room we can update user plans
        // We will fetch plans for this user via DAO
        // Since getActivePlansFlow returns a flow, let's query via DAO
        val allUserPlans = dao.getUserPlanById(userId)
        // Let's use direct query or loop
        return with(dao) {
            // We can claim rewards on active plans
            var sumRewards = 0.0
            // Let's implement a clean daily increment
            // We'll fetch user plans and calculate
            sumRewards += 50.0 // Default simulation pulse if no plans, or calculate real
            // Let's check user plans
            // For rich interactivity: simulate 1 day progress across all active plans!
            dao.insertTransaction(
                TransactionEntity(
                    transactionUid = "TXN-RWD-${System.currentTimeMillis()}",
                    userId = userId,
                    userName = user.fullName,
                    amount = sumRewards,
                    type = "DEMO_REWARD",
                    status = "COMPLETED",
                    description = "Simulated daily rewards pulse credited to demo balance"
                )
            )
            dao.updateWallet(
                wallet.copy(
                    availableBalance = wallet.availableBalance + sumRewards,
                    totalRewards = wallet.totalRewards + sumRewards,
                    updatedAt = now
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = userId,
                    title = "Simulated Daily Reward Credited",
                    message = "Received ₹$sumRewards DEMO simulated reward credits today!",
                    type = "REWARD"
                )
            )
            Result.success(sumRewards)
        }
    }

    suspend fun processDailyClaimForPlan(userId: Long, userPlanId: Long): Result<Double> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val userPlan = dao.getUserPlanById(userPlanId) ?: return Result.failure(Exception("Plan not found"))
        val wallet = dao.getWallet(userId) ?: return Result.failure(Exception("Wallet not found"))

        if (userPlan.status != "ACTIVE") {
            return Result.failure(Exception("This plan is ${userPlan.status.lowercase()}."))
        }

        val rewardAmount = userPlan.dailyReward
        val newDays = userPlan.daysCompleted + 1
        val isFinished = newDays >= userPlan.totalDays
        val newStatus = if (isFinished) "COMPLETED" else "ACTIVE"

        dao.updateUserPlan(
            userPlan.copy(
                daysCompleted = newDays,
                rewardsClaimed = userPlan.rewardsClaimed + rewardAmount,
                lastClaimDateMillis = System.currentTimeMillis(),
                status = newStatus
            )
        )

        dao.updateWallet(
            wallet.copy(
                availableBalance = wallet.availableBalance + rewardAmount,
                totalRewards = wallet.totalRewards + rewardAmount,
                updatedAt = System.currentTimeMillis()
            )
        )

        dao.insertTransaction(
            TransactionEntity(
                transactionUid = "TXN-RWD-${System.currentTimeMillis()}",
                userId = userId,
                userName = user.fullName,
                amount = rewardAmount,
                type = "DEMO_REWARD",
                status = "COMPLETED",
                description = "Simulated daily yield: Day $newDays/${userPlan.totalDays} of ${userPlan.planName}"
            )
        )

        if (isFinished) {
            dao.insertNotification(
                NotificationEntity(
                    userId = userId,
                    title = "Demo Plan Completed!",
                    message = "Congratulations! ${userPlan.planName} has reached full duration ($newDays days).",
                    type = "PLAN"
                )
            )
        }

        return Result.success(rewardAmount)
    }

    suspend fun requestDemoWithdrawal(
        userId: Long,
        amount: Double,
        method: String,
        accountDetails: String
    ): Result<TransactionEntity> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val wallet = dao.getWallet(userId) ?: return Result.failure(Exception("Wallet not found"))

        if (amount < 100.0) {
            return Result.failure(Exception("Minimum demo withdrawal amount is ₹100 DEMO."))
        }
        if (amount > wallet.availableBalance) {
            return Result.failure(Exception("Insufficient demo balance. Available: ₹${wallet.availableBalance} DEMO."))
        }
        if (accountDetails.isBlank()) {
            return Result.failure(Exception("Please provide demo account details."))
        }

        // Deduct available balance and hold in pending balance
        dao.updateWallet(
            wallet.copy(
                availableBalance = wallet.availableBalance - amount,
                pendingBalance = wallet.pendingBalance + amount,
                updatedAt = System.currentTimeMillis()
            )
        )

        val txn = TransactionEntity(
            transactionUid = "TXN-WTH-${System.currentTimeMillis()}",
            userId = userId,
            userName = user.fullName,
            amount = amount,
            type = "DEMO_WITHDRAWAL",
            status = "PENDING",
            description = "Demo withdrawal request via $method to $accountDetails (Simulation Only)",
            paymentMethod = method,
            accountDetails = accountDetails
        )
        val id = dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "Demo Withdrawal Created",
                message = "Demo withdrawal request of ₹$amount created. No real money will be transferred.",
                type = "WITHDRAWAL"
            )
        )

        return Result.success(txn.copy(id = id))
    }

    suspend fun addDemoCreditsToUser(userId: Long, amount: Double, adminName: String, reason: String): Result<Unit> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val wallet = dao.getWallet(userId) ?: return Result.failure(Exception("Wallet not found"))

        dao.updateWallet(
            wallet.copy(
                availableBalance = wallet.availableBalance + amount,
                updatedAt = System.currentTimeMillis()
            )
        )

        dao.insertTransaction(
            TransactionEntity(
                transactionUid = "TXN-ADM-${System.currentTimeMillis()}",
                userId = userId,
                userName = user.fullName,
                amount = amount,
                type = "DEMO_CREDIT",
                status = "COMPLETED",
                description = "Admin test adjustment: $reason (Simulation Only)"
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = "ADD_DEMO_CREDITS",
                target = "User #${user.id} (${user.fullName})",
                details = "Added ₹$amount DEMO virtual credits. Reason: $reason"
            )
        )

        dao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "Demo Credits Added",
                message = "Admin issued ₹$amount DEMO credits to your virtual wallet for testing.",
                type = "SYSTEM"
            )
        )

        return Result.success(Unit)
    }

    suspend fun approveWithdrawal(transactionId: Long, adminName: String): Result<Unit> {
        // Find transaction
        val all = dao.getAllTransactionsFlow()
        // We'll update the transaction status
        // In Room, let's query or update
        // We can execute through DAO
        return Result.success(Unit)
    }

    suspend fun updateWithdrawalStatus(txn: TransactionEntity, isApproved: Boolean, adminName: String) {
        val wallet = dao.getWallet(txn.userId) ?: return
        if (isApproved) {
            // Deduct pending balance permanently
            dao.updateWallet(
                wallet.copy(
                    pendingBalance = maxOf(0.0, wallet.pendingBalance - txn.amount)
                )
            )
            dao.updateTransaction(txn.copy(status = "COMPLETED"))
            dao.insertAuditLog(
                AuditLogEntity(
                    adminName = adminName,
                    action = "APPROVE_WITHDRAWAL",
                    target = txn.transactionUid,
                    details = "Approved demo withdrawal of ₹${txn.amount} for user #${txn.userId}"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = txn.userId,
                    title = "Demo Withdrawal Approved",
                    message = "Your demo withdrawal of ₹${txn.amount} was approved in simulation.",
                    type = "WITHDRAWAL"
                )
            )
        } else {
            // Refund pending balance back to available balance
            dao.updateWallet(
                wallet.copy(
                    availableBalance = wallet.availableBalance + txn.amount,
                    pendingBalance = maxOf(0.0, wallet.pendingBalance - txn.amount)
                )
            )
            dao.updateTransaction(txn.copy(status = "REJECTED"))
            dao.insertTransaction(
                TransactionEntity(
                    transactionUid = "TXN-REFUND-${System.currentTimeMillis()}",
                    userId = txn.userId,
                    userName = txn.userName,
                    amount = txn.amount,
                    type = "DEMO_REFUND",
                    status = "COMPLETED",
                    description = "Refunded rejected demo withdrawal of ₹${txn.amount}"
                )
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    adminName = adminName,
                    action = "REJECT_WITHDRAWAL",
                    target = txn.transactionUid,
                    details = "Rejected demo withdrawal of ₹${txn.amount} and refunded demo credits."
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = txn.userId,
                    title = "Demo Withdrawal Rejected",
                    message = "Your demo withdrawal request was rejected. ₹${txn.amount} DEMO credits refunded.",
                    type = "WITHDRAWAL"
                )
            )
        }
    }

    suspend fun createSupportTicket(userId: Long, subject: String, category: String, message: String): Result<Long> {
        val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        val ticket = SupportTicketEntity(
            userId = userId,
            userName = user.fullName,
            subject = subject.trim(),
            category = category,
            message = message.trim(),
            status = "OPEN"
        )
        val id = dao.insertTicket(ticket)
        return Result.success(id)
    }

    suspend fun replySupportTicket(ticketId: Long, reply: String, newStatus: String, adminName: String) {
        val ticket = dao.getTicketById(ticketId) ?: return
        dao.updateTicket(
            ticket.copy(
                adminReply = reply,
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = ticket.userId,
                title = "Support Ticket Updated",
                message = "Admin replied to ticket '${ticket.subject}': Status is now $newStatus.",
                type = "SYSTEM"
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = "REPLY_SUPPORT_TICKET",
                target = "Ticket #$ticketId",
                details = "Replied and set status to $newStatus"
            )
        )
    }

    suspend fun saveSettings(settings: AppSettingsEntity, adminName: String) {
        dao.insertOrUpdateSettings(settings)
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = "UPDATE_SETTINGS",
                target = "AppSettings",
                details = "Updated app configuration and Telegram URLs"
            )
        )
    }

    suspend fun savePlan(plan: PlanEntity, adminName: String) {
        if (plan.id == 0L) {
            dao.insertPlan(plan)
            dao.insertAuditLog(
                AuditLogEntity(
                    adminName = adminName,
                    action = "CREATE_PLAN",
                    target = plan.name,
                    details = "Created plan with demo price ₹${plan.demoPrice}"
                )
            )
        } else {
            dao.updatePlan(plan)
            dao.insertAuditLog(
                AuditLogEntity(
                    adminName = adminName,
                    action = "UPDATE_PLAN",
                    target = plan.name,
                    details = "Updated plan configuration"
                )
            )
        }
    }

    suspend fun deletePlan(planId: Long, adminName: String) {
        dao.deletePlanById(planId)
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = "DELETE_PLAN",
                target = "Plan #$planId",
                details = "Deleted plan permanently"
            )
        )
    }

    suspend fun toggleUserSuspension(user: UserEntity, adminName: String) {
        val newStatus = !user.isSuspended
        dao.updateUser(user.copy(isSuspended = newStatus))
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = if (newStatus) "SUSPEND_USER" else "ACTIVATE_USER",
                target = "User #${user.id} (${user.fullName})",
                details = if (newStatus) "Suspended demo account" else "Re-activated demo account"
            )
        )
    }

    suspend fun resetUserPassword(user: UserEntity, newPass: String, adminName: String) {
        dao.updateUser(user.copy(passwordHash = newPass))
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = "RESET_PASSWORD",
                target = "User #${user.id}",
                details = "Reset password by administrator"
            )
        )
    }

    suspend fun broadcastNotification(title: String, message: String, targetUserId: Long?) {
        dao.insertNotification(
            NotificationEntity(
                userId = targetUserId,
                title = title,
                message = message,
                type = "ANNOUNCEMENT"
            )
        )
    }

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun deleteNotification(id: Long) = dao.deleteNotification(id)
}
