package com.hydration.app.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydration.app.ui.analytics.AnalyticsScreen
import com.hydration.app.ui.home.HomeScreen
import com.hydration.app.ui.onboarding.OnboardingScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val todayLogs by viewModel.todayLogs.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val streak by viewModel.streak.collectAsState()

    var selectedTab by mutableStateOf(0)

    if (userProfile == null) {
        OnboardingScreen(
            onComplete = { weight, activity, climate, wake, sleep ->
                viewModel.saveOnboarding(weight, activity, climate, wake, sleep) {}
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Info, contentDescription = "Analytics") },
                        label = { Text("Analytics") }
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (selectedTab == 0) {
                    HomeScreen(
                        userProfile = userProfile,
                        todayLogs = todayLogs,
                        streak = streak,
                        onAddLog = { amount, type -> viewModel.addWaterLog(amount, type) },
                        onDeleteLog = { log -> viewModel.deleteWaterLog(log) }
                    )
                } else {
                    AnalyticsScreen(
                        userProfile = userProfile,
                        allLogs = allLogs,
                        streak = streak
                    )
                }
            }
        }
    }
}
