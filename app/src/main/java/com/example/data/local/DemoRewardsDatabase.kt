package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        WalletEntity::class,
        PlanEntity::class,
        UserPlanEntity::class,
        TransactionEntity::class,
        NotificationEntity::class,
        SupportTicketEntity::class,
        AppSettingsEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DemoRewardsDatabase : RoomDatabase() {

    abstract fun demoRewardsDao(): DemoRewardsDao

    companion object {
        @Volatile
        private var INSTANCE: DemoRewardsDatabase? = null

        fun getDatabase(context: Context): DemoRewardsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DemoRewardsDatabase::class.java,
                    "demo_rewards_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
