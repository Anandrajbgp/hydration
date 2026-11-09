package com.hydration.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val weightKg: Double,
    val activityLevel: String, // 'sedentary', 'moderate', 'active'
    val climate: String, // 'cold', 'temperate', 'hot'
    val wakeTime: String, // HH:MM
    val sleepTime: String, // HH:MM
    val dailyGoalMl: Int
)
