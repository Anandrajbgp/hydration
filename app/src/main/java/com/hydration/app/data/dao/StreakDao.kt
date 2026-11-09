package com.hydration.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hydration.app.data.entity.Streak
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE id = 1")
    fun getStreak(): Flow<Streak?>

    @Query("SELECT * FROM streak WHERE id = 1")
    suspend fun getStreakSync(): Streak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: Streak)
}
