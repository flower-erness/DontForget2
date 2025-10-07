@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.dontforget.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dontforget.data.model.Subscription
import com.example.dontforget.viewmodel.SubscriptionViewModel
import java.util.*

@Composable
fun AddEditSubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    existing: Subscription? = null
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var cost by remember { mutableStateOf(existing?.cost?.toString() ?: "") }
    var dueDate by remember { mutableStateOf(existing?.nextDue ?: System.currentTimeMillis()) }
    var cycle by remember { mutableStateOf(existing?.cycle ?: Subscription.Cycle.MONTHLY) }
    var reminder by remember { mutableStateOf(existing?.reminderEnabled ?: true) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = dueDate }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            dueDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (existing == null) "Add Subscription" else "Edit Subscription"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val sub = Subscription(
                            id = existing?.id ?: 0,
                            name = name,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            cycle = cycle,
                            nextDue = dueDate,
                            reminderEnabled = reminder
                        )
                        if (existing == null) viewModel.addSubscription(sub)
                        else viewModel.updateSubscription(sub)
                        onSave()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Subscription Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Date Picker
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Next Due: " +
                            android.text.format.DateFormat.format("yyyy-MM-dd", dueDate)
                )
            }

            CycleDropdown(selected = cycle, onSelected = { cycle = it })

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = reminder,
                    onCheckedChange = { reminder = it }
                )
                Text("Enable reminder")
            }
        }
    }
}

@Composable
fun CycleDropdown(selected: Subscription.Cycle, onSelected: (Subscription.Cycle) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            label = { Text("Billing Cycle") },
            readOnly = true,
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Subscription.Cycle.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
