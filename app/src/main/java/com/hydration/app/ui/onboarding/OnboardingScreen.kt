package com.hydration.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (Double, String, String, String, String) -> Unit
) {
    var weightText by mutableStateOf("")
    var selectedActivity by mutableStateOf("sedentary")
    var selectedClimate by mutableStateOf("temperate")
    var wakeTime by mutableStateOf("07:00")
    var sleepTime by mutableStateOf("23:00")
    var errorMessage by mutableStateOf("")

    val activities = listOf("sedentary", "moderate", "active")
    val climates = listOf("cold", "temperate", "hot")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to Hydration 💧", fontWeight = FontWeight.Bold) },
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
            Text(
                text = "Let's personalize your daily water goal based on your body and lifestyle.",
                style = MaterialTheme.typography.bodyLarge
            )

            // Weight input
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it; errorMessage = "" },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Activity level
            Text("Activity Level", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activities.forEach { act ->
                    FilterChip(
                        selected = selectedActivity == act,
                        onClick = { selectedActivity = act },
                        label = { Text(act.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Climate
            Text("Climate", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                climates.forEach { clim ->
                    FilterChip(
                        selected = selectedClimate == clim,
                        onClick = { selectedClimate = clim },
                        label = { Text(clim.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Wake Time & Sleep Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = wakeTime,
                    onValueChange = { wakeTime = it },
                    label = { Text("Wake Time (HH:MM)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = sleepTime,
                    onValueChange = { sleepTime = it },
                    label = { Text("Sleep Time (HH:MM)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val weight = weightText.toDoubleOrNull()
                    if (weight == null || weight <= 0) {
                        errorMessage = "Please enter a valid positive weight in kg."
                        return@Button
                    }
                    onComplete(weight, selectedActivity, selectedClimate, wakeTime, sleepTime)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Calculate Goal & Start", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
