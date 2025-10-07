@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.dontforget.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dontforget.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoggedIn: () -> Unit, authVm: AuthViewModel = viewModel()) {
    val state by authVm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is com.example.dontforget.viewmodel.AuthState.LoggedIn) {
            onLoggedIn()
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authVm.login(email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* navigate to register by parent NavHost via passed lambda or via NavController */ }) {
            Text("Don't have an account? Register")
        }
        if (state is com.example.dontforget.viewmodel.AuthState.Loading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }
        if (state is com.example.dontforget.viewmodel.AuthState.Error) {
            Text((state as com.example.dontforget.viewmodel.AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, authVm: AuthViewModel = viewModel()) {
    val state by authVm.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is com.example.dontforget.viewmodel.AuthState.LoggedIn) {
            onRegistered()
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authVm.register(name, email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
        if (state is com.example.dontforget.viewmodel.AuthState.Loading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }
        if (state is com.example.dontforget.viewmodel.AuthState.Error) {
            Text((state as com.example.dontforget.viewmodel.AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}
