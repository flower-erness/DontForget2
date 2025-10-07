@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.dontforget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dontforget.data.model.Subscription
import com.example.dontforget.ui.screens.MainNavHost
import com.example.dontforget.ui.theme.DontForgetTheme
import com.example.dontforget.viewmodel.SubscriptionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DontForgetTheme {
                MainNavHost()
            }
        }
    }
}

@Composable
fun DontForgetApp(vm: SubscriptionViewModel = viewModel()) {
    val subs by vm.subscriptions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Don't Forget Subscriptions") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                vm.addSubscription(
                    Subscription(
                        name = "Netflix",
                        cost = 15.99,
                        nextDue = System.currentTimeMillis() + 86400000L
                    )
                )
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Active Subscriptions:", style = MaterialTheme.typography.headlineSmall)

            subs.forEach { sub ->
                Text("• ${sub.name} - $${sub.cost}")
            }
        }
    }
}
