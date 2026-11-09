package com.hydration.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak")
data class Streak(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 0,
    val lastCompletedDate: String = "" // YYYY-MM-DD
)
