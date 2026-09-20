package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DemoRewardsDao {

    // User Operations
    @Query("SELECT * FROM users WHERE email = :identifier OR mobile = :identifier LIMIT 1")
    suspend fun getUserByEmailOrMobile(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registeredAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE referredBy = :code ORDER BY registeredAt DESC")
    fun getReferredUsers(code: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Wallet Operations
    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    fun getWalletFlow(userId: Long): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    suspend fun getWallet(userId: Long): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("SELECT SUM(availableBalance) FROM wallets")
    fun getTotalDemoBalanceFlow(): Flow<Double?>

    // Plan Operations
    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY demoPrice ASC")
    fun getActivePlansFlow(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans ORDER BY id ASC")
    fun getAllPlansFlow(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE id = :planId LIMIT 1")
    suspend fun getPlanById(planId: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<PlanEntity>)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :planId")
    suspend fun deletePlanById(planId: Long)

    // User Plans (Investments)
    @Query("SELECT * FROM user_plans WHERE userId = :userId ORDER BY activatedAt DESC")
    fun getUserPlansFlow(userId: Long): Flow<List<UserPlanEntity>>

    @Query("SELECT * FROM user_plans WHERE status = 'ACTIVE'")
    fun getAllActiveUserPlansFlow(): Flow<List<UserPlanEntity>>

    @Query("SELECT * FROM user_plans ORDER BY activatedAt DESC")
    fun getAllUserPlansFlow(): Flow<List<UserPlanEntity>>

    @Query("SELECT * FROM user_plans WHERE id = :userPlanId LIMIT 1")
    suspend fun getUserPlanById(userPlanId: Long): UserPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPlan(userPlan: UserPlanEntity): Long

    @Update
    suspend fun updateUserPlan(userPlan: UserPlanEntity)

    // Transactions
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserTransactionsFlow(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'DEMO_WITHDRAWAL' AND status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingWithdrawalsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId IS NULL ORDER BY createdAt DESC")
    fun getNotificationsFlow(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    // Support Tickets
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getUserTicketsFlow(userId: Long): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets ORDER BY updatedAt DESC")
    fun getAllTicketsFlow(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE id = :id LIMIT 1")
    suspend fun getTicketById(id: Long): SupportTicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity): Long

    @Update
    suspend fun updateTicket(ticket: SupportTicketEntity)

    // Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long
}
