package com.hydration.app.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydration.app.data.HydrationCalculator
import com.hydration.app.data.entity.Streak
import com.hydration.app.data.entity.UserProfile
import com.hydration.app.data.entity.WaterLog
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    userProfile: UserProfile?,
    allLogs: List<WaterLog>,
    streak: Streak?
) {
    val goalMl = userProfile?.dailyGoalMl ?: 2000

    // Compute last 7 days intake
    val calendar = Calendar.getInstance()
    val dailyIntakes = mutableListOf<Pair<String, Int>>()

    for (i in 6 downTo 0) {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
        val dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

        val startOfDay = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val dayLogs = allLogs.filter { it.timestamp in startOfDay..endOfDay }
        val totalEffective = dayLogs.sumOf { HydrationCalculator.calculateEffectiveHydration(it.amountMl, it.beverageType) }
        dailyIntakes.add(Pair(dayName, totalEffective))
    }

    val avgIntake = if (dailyIntakes.isNotEmpty()) dailyIntakes.sumOf { it.second } / dailyIntakes.size else 0
    val daysCompletedCount = dailyIntakes.count { it.second >= goalMl }
    val completionRate = (daysCompletedCount.toFloat() / 7f * 100f).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & History 📊", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Weekly Water Intake (ml)", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            // Weekly Bar Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WeeklyBarChart(dailyIntakes = dailyIntakes, goalMl = goalMl)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Key Metrics", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            // Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Average Intake",
                    value = "$avgIntake ml",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Goal Completion",
                    value = "$completionRate%",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Current Streak",
                    value = "${streak?.currentStreak ?: 0} Days 🔥",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Daily Target",
                    value = "$goalMl ml",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun WeeklyBarChart(dailyIntakes: List<Pair<String, Int>>, goalMl: Int) {
    val maxVal = maxOf(goalMl, dailyIntakes.maxOfOrNull { it.second } ?: goalMl).toFloat()
    val barColor = MaterialTheme.colorScheme.primary
    val goalLineColor = MaterialTheme.colorScheme.error

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / (dailyIntakes.size * 2f)

        // Draw goal line
        val goalY = height * (1f - (goalMl.toFloat() / maxVal).coerceIn(0f, 1f))
        drawLine(
            color = goalLineColor,
            start = Offset(0f, goalY),
            end = Offset(width, goalY),
            strokeWidth = 3f
        )

        dailyIntakes.forEachIndexed { index, (day, intake) ->
            val barHeight = height * (intake.toFloat() / maxVal).coerceIn(0f, 1f)
            val left = index * (width / dailyIntakes.size) + (barWidth / 2f)
            val top = height - barHeight

            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
