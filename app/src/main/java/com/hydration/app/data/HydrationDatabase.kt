package com.hydration.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hydration.app.data.dao.StreakDao
import com.hydration.app.data.dao.UserProfileDao
import com.hydration.app.data.dao.WaterLogDao
import com.hydration.app.data.entity.Streak
import com.hydration.app.data.entity.UserProfile
import com.hydration.app.data.entity.WaterLog

@Database(entities = [UserProfile::class, WaterLog::class, Streak::class], version = 1, exportSchema = false)
abstract class HydrationDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: HydrationDatabase? = null

        fun getDatabase(context: Context): HydrationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HydrationDatabase::class.java,
                    "hydration_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
