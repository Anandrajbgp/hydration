package com.hydration.app.data

import com.hydration.app.data.dao.StreakDao
import com.hydration.app.data.dao.UserProfileDao
import com.hydration.app.data.dao.WaterLogDao
import com.hydration.app.data.entity.Streak
import com.hydration.app.data.entity.UserProfile
import com.hydration.app.data.entity.WaterLog
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class HydrationRepository(
    private val userProfileDao: UserProfileDao,
    private val waterLogDao: WaterLogDao,
    private val streakDao: StreakDao
) {
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()
    val streak: Flow<Streak?> = streakDao.getStreak()

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun getUserProfileSync(): UserProfile? {
        return userProfileDao.getUserProfileSync()
    }

    suspend fun addWaterLog(amountMl: Int, beverageType: String) {
        val log = WaterLog(amountMl = amountMl, beverageType = beverageType)
        waterLogDao.insertLog(log)
        checkAndUpdateStreak()
    }

    suspend fun deleteWaterLog(log: WaterLog) {
        waterLogDao.deleteLog(log)
        checkAndUpdateStreak()
    }

    fun getTodayLogs(): Flow<List<WaterLog>> {
        val (start, end) = getTodayStartAndEndMillis()
        return waterLogDao.getLogsForTimeRange(start, end)
    }

    suspend fun getTodayEffectiveIntakeSync(): Int {
        val (start, end) = getTodayStartAndEndMillis()
        val logs = waterLogDao.getLogsForTimeRangeSync(start, end)
        return logs.sumOf { HydrationCalculator.calculateEffectiveHydration(it.amountMl, it.beverageType) }
    }

    suspend fun checkAndUpdateStreak() {
        val profile = userProfileDao.getUserProfileSync() ?: return
        val (startToday, endToday) = getTodayStartAndEndMillis()
        val todayLogs = waterLogDao.getLogsForTimeRangeSync(startToday, endToday)
        val todayEffective = todayLogs.sumOf { HydrationCalculator.calculateEffectiveHydration(it.amountMl, it.beverageType) }

        val calendar = Calendar.getInstance()
        val todayStr = formatDate(calendar.timeInMillis)

        val currentStreakObj = streakDao.getStreakSync() ?: Streak(currentStreak = 0, lastCompletedDate = "")

        if (todayEffective >= profile.dailyGoalMl) {
            // Goal met today
            if (currentStreakObj.lastCompletedDate != todayStr) {
                // Check if last completed date was yesterday
                val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                val yesterdayStr = formatDate(yesterdayCal.timeInMillis)

                val newStreak = if (currentStreakObj.lastCompletedDate == yesterdayStr) {
                    currentStreakObj.currentStreak + 1
                } else if (currentStreakObj.lastCompletedDate.isEmpty()) {
                    1
                } else {
                    // streak broken or already counted
                    currentStreakObj.currentStreak.coerceAtLeast(1)
                }

                streakDao.insertOrUpdateStreak(
                    Streak(id = 1, currentStreak = newStreak, lastCompletedDate = todayStr)
                )
            }
        } else {
            // If goal not met today, check if yesterday was missed (unless yesterday goal was met)
            // Keep streak as is or reset if needed. For user friendliness, we don't reset until a full day passes without meeting goal.
        }
    }

    private fun getTodayStartAndEndMillis(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    private fun formatDate(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}
