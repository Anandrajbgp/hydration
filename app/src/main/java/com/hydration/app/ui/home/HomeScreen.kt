package com.hydration.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydration.app.data.HydrationCalculator
import com.hydration.app.data.entity.Streak
import com.hydration.app.data.entity.UserProfile
import com.hydration.app.data.entity.WaterLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    todayLogs: List<WaterLog>,
    streak: Streak?,
    onAddLog: (Int, String) -> Unit,
    onDeleteLog: (WaterLog) -> Unit
) {
    var showCustomDialog by mutableStateOf(false)

    val goalMl = userProfile?.dailyGoalMl ?: 2000
    val currentEffectiveMl = todayLogs.sumOf {
        HydrationCalculator.calculateEffectiveHydration(it.amountMl, it.beverageType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hydration Dashboard 💧", fontWeight = FontWeight.Bold) },
                actions = {
                    // Streak badge
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 ${streak?.currentStreak ?: 0}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Wave & Progress Animation Centerpiece
            WaveLoadingView(
                currentMl = currentEffectiveMl,
                goalMl = goalMl,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Log Buttons (Glass 250ml, Mug 350ml, Bottle 500ml, Custom +)
            Text("Quick Log", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onAddLog(250, "water") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🥛 250ml")
                }
                Button(
                    onClick = { onAddLog(350, "water") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("☕ 350ml")
                }
                Button(
                    onClick = { onAddLog(500, "water") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🍼 500ml")
                }
                IconButton(
                    onClick = { showCustomDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Custom Log")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's History Feed
            Text(
                "Today's History",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (todayLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No water logged yet today.\nTap a quick button above to start!",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todayLogs) { log ->
                        WaterLogItem(log = log, onDelete = { onDeleteLog(log) })
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        QuickLogDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { amount, bev ->
                onAddLog(amount, bev)
                showCustomDialog = false
            }
        )
    }
}

@Composable
fun WaterLogItem(log: WaterLog, onDelete: () -> Unit) {
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    val effective = HydrationCalculator.calculateEffectiveHydration(log.amountMl, log.beverageType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${log.amountMl} ml (${log.beverageType.replaceFirstChar { it.uppercase() }})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Effective: $effective ml • $timeFormatted",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete log", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
