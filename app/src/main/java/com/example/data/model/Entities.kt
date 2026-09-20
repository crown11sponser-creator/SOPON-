package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val mobile: String,
    val email: String,
    val passwordHash: String,
    val referralCode: String,
    val referredBy: String? = null,
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val userId: Long,
    val availableBalance: Double = 1500.0,
    val pendingBalance: Double = 0.0,
    val totalRewards: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val referralRewards: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val productIcon: String,
    val demoPrice: Double,
    val durationDays: Int,
    val dailySimulatedReward: Double,
    val totalSimulatedReward: Double,
    val maxActivations: Int = 10,
    val isActive: Boolean = true
)

@Entity(tableName = "user_plans")
data class UserPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val planId: Long,
    val planName: String,
    val investedAmount: Double,
    val dailyReward: Double,
    val totalReward: Double,
    val totalDays: Int,
    val daysCompleted: Int = 0,
    val rewardsClaimed: Double = 0.0,
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val lastClaimDateMillis: Long = 0L,
    val status: String = "ACTIVE" // ACTIVE, COMPLETED, CANCELLED
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionUid: String,
    val userId: Long,
    val userName: String,
    val amount: Double,
    val type: String, // DEMO_CREDIT, DEMO_PLAN, DEMO_REWARD, DEMO_REFERRAL, DEMO_WITHDRAWAL, DEMO_REFUND
    val status: String, // COMPLETED, PENDING, REJECTED
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String? = null,
    val accountDetails: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long? = null, // null for broadcast to all users
    val title: String,
    val message: String,
    val type: String, // REWARD, PLAN, REFERRAL, WITHDRAWAL, ANNOUNCEMENT, SYSTEM
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val subject: String,
    val category: String,
    val message: String,
    val adminReply: String? = null,
    val status: String = "OPEN", // OPEN, PENDING, RESOLVED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val appName: String = "Demo Rewards",
    val telegramChannelUrl: String = "https://t.me/demorewards_official",
    val telegramSupportUrl: String = "https://t.me/demorewards_support",
    val telegramAnnouncementUrl: String = "https://t.me/demorewards_alerts",
    val supportEmail: String = "support@demorewards.example",
    val supportPhone: String = "+1 (800) 555-DEMO",
    val currencySymbol: String = "₹",
    val minWithdrawal: Double = 100.0,
    val level1Reward: Double = 10.0,
    val level2Reward: Double = 5.0,
    val level3Reward: Double = 2.0,
    val maintenanceMode: Boolean = false,
    val showDemoBanner: Boolean = true
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminName: String,
    val action: String,
    val target: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
