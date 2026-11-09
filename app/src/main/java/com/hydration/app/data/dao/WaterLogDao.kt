package com.hydration.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hydration.app.data.entity.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insertLog(log: WaterLog)

    @Delete
    suspend fun deleteLog(log: WaterLog)

    @Query("SELECT * FROM water_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_log WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getLogsForTimeRange(startTime: Long, endTime: Long): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_log WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getLogsForTimeRangeSync(startTime: Long, endTime: Long): List<WaterLog>
}
