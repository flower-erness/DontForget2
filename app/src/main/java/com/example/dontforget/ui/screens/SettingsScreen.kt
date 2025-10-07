@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.dontforget.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dontforget.viewmodel.AuthViewModel
import com.example.dontforget.viewmodel.SettingsViewModel


@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = viewModel(),authViewModel: AuthViewModel, onLogout: () -> Unit, onBack: () -> Unit) {
    val notifications by settingsViewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Settings") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Notifications", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = notifications,
                    onCheckedChange = { settingsViewModel.setNotifications(it) })
            }
            // TODO: add language selector, cloud backup toggle etc.


            Divider()

            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}




