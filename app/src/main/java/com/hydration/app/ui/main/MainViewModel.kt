package com.hydration.app.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hydration.app.data.HydrationCalculator
import com.hydration.app.data.HydrationDatabase
import com.hydration.app.data.HydrationRepository
import com.hydration.app.data.entity.Streak
import com.hydration.app.data.entity.UserProfile
import com.hydration.app.data.entity.WaterLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HydrationRepository

    init {
        val db = HydrationDatabase.getDatabase(application)
        repository = HydrationRepository(db.userProfileDao(), db.waterLogDao(), db.streakDao())
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayLogs: StateFlow<List<WaterLog>> = repository.getTodayLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streak: StateFlow<Streak?> = repository.streak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLogs: StateFlow<List<WaterLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveOnboarding(
        weightKg: Double,
        activityLevel: String,
        climate: String,
        wakeTime: String,
        sleepTime: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val goal = HydrationCalculator.calculateDailyGoal(weightKg, activityLevel, climate)
            val profile = UserProfile(
                weightKg = weightKg,
                activityLevel = activityLevel,
                climate = climate,
                wakeTime = wakeTime,
                sleepTime = sleepTime,
                dailyGoalMl = goal
            )
            repository.saveUserProfile(profile)
            onSuccess()
        }
    }

    fun addWaterLog(amountMl: Int, beverageType: String) {
        viewModelScope.launch {
            repository.addWaterLog(amountMl, beverageType)
        }
    }

    fun deleteWaterLog(log: WaterLog) {
        viewModelScope.launch {
            repository.deleteWaterLog(log)
        }
    }
}
