package com.hydration.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var amountText by mutableStateOf("250")
    var selectedBeverage by mutableStateOf("water")
    var errorMessage by mutableStateOf("")

    val beverages = listOf("water", "coffee", "tea", "juice")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Beverage", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; errorMessage = "" },
                    label = { Text("Amount (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Beverage Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    beverages.forEach { bev ->
                        FilterChip(
                            selected = selectedBeverage == bev,
                            onClick = { selectedBeverage = bev },
                            label = { Text(bev.replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toIntOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = "Enter valid amount in ml."
                        return@Button
                    }
                    onConfirm(amount, selectedBeverage)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
