package com.example.data.local

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DemoDataSeeder {

    suspend fun seedInitialDataIfEmpty(dao: DemoRewardsDao) = withContext(Dispatchers.IO) {
        val existingSettings = dao.getSettings()
        if (existingSettings != null) {
            return@withContext // already initialized
        }

        // 1. App Settings
        val settings = AppSettingsEntity(
            id = 1,
            appName = "Demo Rewards",
            telegramChannelUrl = "https://t.me/demorewards_official",
            telegramSupportUrl = "https://t.me/demorewards_support",
            telegramAnnouncementUrl = "https://t.me/demorewards_alerts",
            supportEmail = "support@demorewards.example",
            supportPhone = "+1 (800) 555-DEMO",
            currencySymbol = "₹",
            level1Reward = 10.0,
            level2Reward = 5.0,
            level3Reward = 2.0,
            maintenanceMode = false,
            showDemoBanner = true
        )
        dao.insertOrUpdateSettings(settings)

        // 2. Default Admin
        val adminUser = UserEntity(
            id = 1,
            fullName = "Demo Admin",
            mobile = "9999999999",
            email = "admin@demorewards.example",
            passwordHash = "admin123", // In demo mode for instant testing
            referralCode = "ADMIN999",
            referredBy = null,
            isAdmin = true,
            isSuspended = false,
            registeredAt = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000)
        )
        dao.insertUser(adminUser)
        dao.insertWallet(
            WalletEntity(
                userId = 1,
                availableBalance = 50000.0,
                pendingBalance = 0.0,
                totalRewards = 0.0,
                totalInvestment = 0.0,
                referralRewards = 0.0
            )
        )

        // 3. Primary Demo User
        val primaryUser = UserEntity(
            id = 2,
            fullName = "Alex Mercer",
            mobile = "9876543210",
            email = "demo@example.com",
            passwordHash = "password123",
            referralCode = "USER48291",
            referredBy = "ADMIN999",
            isAdmin = false,
            isSuspended = false,
            registeredAt = System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000)
        )
        dao.insertUser(primaryUser)
        dao.insertWallet(
            WalletEntity(
                userId = 2,
                availableBalance = 2450.0,
                pendingBalance = 200.0,
                totalRewards = 640.0,
                totalInvestment = 1300.0,
                referralRewards = 45.0
            )
        )

        // 4. Nine Additional Demo Users (Total 10+ users)
        val demoNames = listOf(
            "Priya Sharma" to "9876500001",
            "David Chen" to "9876500002",
            "Ananya Patel" to "9876500003",
            "Marcus Vance" to "9876500004",
            "Kavita Reddy" to "9876500005",
            "Liam O'Connor" to "9876500006",
            "Sneha Rao" to "9876500007",
            "Arjun Mehta" to "9876500008",
            "Zara Khan" to "9876500009"
        )

        demoNames.forEachIndexed { index, (name, mob) ->
            val uId = (index + 3).toLong()
            val code = "REF${10000 + uId}"
            // some users referred by Alex Mercer (USER48291) to populate his team!
            val refBy = if (index < 4) "USER48291" else "ADMIN999"
            val u = UserEntity(
                id = uId,
                fullName = name,
                mobile = mob,
                email = "${name.lowercase().replace(" ", ".")}@demo.example",
                passwordHash = "demo123",
                referralCode = code,
                referredBy = refBy,
                isAdmin = false,
                isSuspended = false,
                registeredAt = System.currentTimeMillis() - ((12 - index) * 24 * 60 * 60 * 1000L)
            )
            dao.insertUser(u)
            dao.insertWallet(
                WalletEntity(
                    userId = uId,
                    availableBalance = 1200.0 + (index * 150.0),
                    pendingBalance = 0.0,
                    totalRewards = 120.0 + (index * 30.0),
                    totalInvestment = 500.0 + (index * 200.0),
                    referralRewards = 20.0 * index
                )
            )
        }

        // 5. Five Demo Plans
        val demoPlans = listOf(
            PlanEntity(
                id = 1,
                name = "Starter Solar Plan",
                description = "Simulated solar device rewards plan. Daily virtual credit reward generation for beginners.",
                productIcon = "solar_power",
                demoPrice = 300.0,
                durationDays = 30,
                dailySimulatedReward = 20.0,
                totalSimulatedReward = 600.0,
                maxActivations = 10,
                isActive = true
            ),
            PlanEntity(
                id = 2,
                name = "Growth Cloud Node",
                description = "Virtual cloud compute simulation. Higher simulated daily rewards for intermediate trial.",
                productIcon = "dns",
                demoPrice = 1000.0,
                durationDays = 45,
                dailySimulatedReward = 75.0,
                totalSimulatedReward = 3375.0,
                maxActivations = 5,
                isActive = true
            ),
            PlanEntity(
                id = 3,
                name = "VIP Power Grid",
                description = "Demonstration heavy utility simulation plan with high virtual yield demo credits.",
                productIcon = "electric_bolt",
                demoPrice = 3000.0,
                durationDays = 60,
                dailySimulatedReward = 250.0,
                totalSimulatedReward = 15000.0,
                maxActivations = 3,
                isActive = true
            ),
            PlanEntity(
                id = 4,
                name = "Alpha Drone Fleet",
                description = "Autonomous delivery drone unit simulation with enhanced simulated demo reward cycles.",
                productIcon = "flight_takeoff",
                demoPrice = 5000.0,
                durationDays = 90,
                dailySimulatedReward = 450.0,
                totalSimulatedReward = 40500.0,
                maxActivations = 2,
                isActive = true
            ),
            PlanEntity(
                id = 5,
                name = "Max Elite Farm",
                description = "Top-tier high-capacity green datacenter simulation for maximum virtual demo testing.",
                productIcon = "hub",
                demoPrice = 10000.0,
                durationDays = 120,
                dailySimulatedReward = 1000.0,
                totalSimulatedReward = 120000.0,
                maxActivations = 1,
                isActive = true
            )
        )
        dao.insertPlans(demoPlans)

        // 6. User Plans for Alex Mercer (User 2)
        val now = System.currentTimeMillis()
        val userPlan1 = UserPlanEntity(
            id = 1,
            userId = 2,
            planId = 1,
            planName = "Starter Solar Plan",
            investedAmount = 300.0,
            dailyReward = 20.0,
            totalReward = 600.0,
            totalDays = 30,
            daysCompleted = 12,
            rewardsClaimed = 240.0,
            activatedAt = now - (12L * 24 * 60 * 60 * 1000),
            expiresAt = now + (18L * 24 * 60 * 60 * 1000),
            lastClaimDateMillis = now - (1L * 24 * 60 * 60 * 1000),
            status = "ACTIVE"
        )
        val userPlan2 = UserPlanEntity(
            id = 2,
            userId = 2,
            planId = 2,
            planName = "Growth Cloud Node",
            investedAmount = 1000.0,
            dailyReward = 75.0,
            totalReward = 3375.0,
            totalDays = 45,
            daysCompleted = 5,
            rewardsClaimed = 375.0,
            activatedAt = now - (5L * 24 * 60 * 60 * 1000),
            expiresAt = now + (40L * 24 * 60 * 60 * 1000),
            lastClaimDateMillis = now - (1L * 24 * 60 * 60 * 1000),
            status = "ACTIVE"
        )
        dao.insertUserPlan(userPlan1)
        dao.insertUserPlan(userPlan2)

        // 7. Demo Transactions for User 2
        val txns = listOf(
            TransactionEntity(
                transactionUid = "TXN-DEMO-001",
                userId = 2,
                userName = "Alex Mercer",
                amount = 2000.0,
                type = "DEMO_CREDIT",
                status = "COMPLETED",
                description = "Initial demo test credits granted upon registration (Simulation)",
                timestamp = now - (15L * 24 * 60 * 60 * 1000)
            ),
            TransactionEntity(
                transactionUid = "TXN-DEMO-002",
                userId = 2,
                userName = "Alex Mercer",
                amount = 300.0,
                type = "DEMO_PLAN",
                status = "COMPLETED",
                description = "Activated Starter Solar Plan with virtual demo credits",
                timestamp = now - (12L * 24 * 60 * 60 * 1000)
            ),
            TransactionEntity(
                transactionUid = "TXN-DEMO-003",
                userId = 2,
                userName = "Alex Mercer",
                amount = 1000.0,
                type = "DEMO_PLAN",
                status = "COMPLETED",
                description = "Activated Growth Cloud Node with virtual demo credits",
                timestamp = now - (5L * 24 * 60 * 60 * 1000)
            ),
            TransactionEntity(
                transactionUid = "TXN-DEMO-004",
                userId = 2,
                userName = "Alex Mercer",
                amount = 95.0,
                type = "DEMO_REWARD",
                status = "COMPLETED",
                description = "Simulated daily yield credited from active demo plans",
                timestamp = now - (1L * 24 * 60 * 60 * 1000)
            ),
            TransactionEntity(
                transactionUid = "TXN-DEMO-005",
                userId = 2,
                userName = "Alex Mercer",
                amount = 25.0,
                type = "DEMO_REFERRAL",
                status = "COMPLETED",
                description = "Simulated Level 1 demo referral bonus from Priya Sharma",
                timestamp = now - (3L * 24 * 60 * 60 * 1000)
            ),
            TransactionEntity(
                transactionUid = "TXN-DEMO-006",
                userId = 2,
                userName = "Alex Mercer",
                amount = 200.0,
                type = "DEMO_WITHDRAWAL",
                status = "PENDING",
                description = "Demo withdrawal request to UPI: alex@demoupi (No real money)",
                timestamp = now - (2L * 3600 * 1000),
                paymentMethod = "UPI DEMO",
                accountDetails = "alex@demoupi"
            )
        )
        txns.forEach { dao.insertTransaction(it) }

        // 8. Demo Notifications
        val notifs = listOf(
            NotificationEntity(
                userId = 2,
                title = "Welcome to Demo Rewards",
                message = "Explore simulated product plans and virtual reward cycles. No real money is involved.",
                type = "SYSTEM",
                isRead = true,
                createdAt = now - (15L * 24 * 60 * 60 * 1000)
            ),
            NotificationEntity(
                userId = 2,
                title = "Simulated Daily Reward Available",
                message = "Your active Starter Solar Plan and Growth Cloud Node have generated ₹95 DEMO credits.",
                type = "REWARD",
                isRead = false,
                createdAt = now - (4L * 3600 * 1000)
            ),
            NotificationEntity(
                userId = 2,
                title = "New Team Member Joined",
                message = "Priya Sharma registered with your referral code USER48291. Level 1 Demo bonus credited.",
                type = "REFERRAL",
                isRead = false,
                createdAt = now - (3L * 24 * 60 * 60 * 1000)
            ),
            NotificationEntity(
                userId = null, // Global
                title = "Demo Platform Version 1.0",
                message = "All features are currently operating in pure simulation demo mode.",
                type = "ANNOUNCEMENT",
                isRead = false,
                createdAt = now - (1L * 24 * 60 * 60 * 1000)
            )
        )
        notifs.forEach { dao.insertNotification(it) }

        // 9. Demo Support Tickets
        val tickets = listOf(
            SupportTicketEntity(
                userId = 2,
                userName = "Alex Mercer",
                subject = "How does the simulated reward cycle work?",
                category = "Demo Plan",
                message = "Can you confirm if daily demo rewards auto-credit or if I can test claiming manually?",
                adminReply = "Hello Alex! Daily rewards can be simulated at any time from your dashboard or auto-calculated daily. Remember this is 100% demo simulation credits.",
                status = "RESOLVED",
                createdAt = now - (7L * 24 * 60 * 60 * 1000),
                updatedAt = now - (6L * 24 * 60 * 60 * 1000)
            ),
            SupportTicketEntity(
                userId = 2,
                userName = "Alex Mercer",
                subject = "Testing Demo Withdrawal Approval",
                category = "Demo Wallet",
                message = "I submitted a demo withdrawal of ₹200 to test the simulated flow.",
                adminReply = null,
                status = "OPEN",
                createdAt = now - (2L * 3600 * 1000),
                updatedAt = now - (2L * 3600 * 1000)
            )
        )
        tickets.forEach { dao.insertTicket(it) }

        // 10. Initial Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = "System Initializer",
                action = "SYSTEM_SEED",
                target = "Database",
                details = "Initialized 10 demo users, 5 plans, sample demo transactions, and audit records in Demo Mode.",
                timestamp = now
            )
        )
    }
}
