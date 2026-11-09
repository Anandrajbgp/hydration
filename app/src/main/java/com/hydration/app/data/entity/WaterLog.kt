package com.hydration.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_log")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val beverageType: String // 'water', 'coffee', 'tea', 'juice'
)
