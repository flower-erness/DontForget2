@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.dontforget.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.dontforget.data.repository.AuthRepository
import com.example.dontforget.viewmodel.AuthViewModel
import com.example.dontforget.viewmodel.SettingsViewModel

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authRepo = remember { AuthRepository(context) }

    // decide start destination synchronously using SecureStorage (fast)
    val startDestination = if (authRepo.getToken() != null) "list" else "login"

    val authVm: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoggedIn = {
                navController.navigate("list") {
                    popUpTo("login") { inclusive = true }
                }
            },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegistered = {
                    navController.navigate("list") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                authVm = viewModel()
            )
        }
        composable("list") {
            val vm: com.example.dontforget.viewmodel.SubscriptionViewModel = viewModel()
            val subs by vm.subscriptions.collectAsState()
            ListScreen(subs = subs, viewModel = vm, onAddClick = { navController.navigate("add") }, onSettingsClick = { navController.navigate("settings") })
        }
        composable("add") {
            val vm: com.example.dontforget.viewmodel.SubscriptionViewModel = viewModel()
            AddEditSubscriptionScreen(viewModel = vm, onSave = { navController.popBackStack() }, onCancel = { navController.popBackStack() })
        }
        composable("settings") {
            val settingsVm: SettingsViewModel = viewModel()
            val authVm: AuthViewModel = viewModel()

            SettingsScreen(
                settingsViewModel = settingsVm,
                authViewModel = authVm,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true } // Clear back stack
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

    }
}
