package com.example.dontforget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dontforget.data.network.UserDto
import com.example.dontforget.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class LoggedIn(val userJson: String) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app.applicationContext)

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    init {
        // if token exists, consider logged-in (you may fetch /me to verify)
        val t = repo.getToken()
        if (t != null) {
            _state.value = AuthState.LoggedIn(repo.getToken() ?: "")
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            when (val r = repo.register(name, email, password)) {
                is com.example.dontforget.data.repository.AuthResult.Success -> {
                    // store userJson via storage done in repo already
                    _state.value = AuthState.LoggedIn(com.google.gson.Gson().toJson(r.user))
                }
                is com.example.dontforget.data.repository.AuthResult.Failure -> {
                    _state.value = AuthState.Error(r.message)
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            when (val r = repo.login(email, password)) {
                is com.example.dontforget.data.repository.AuthResult.Success -> {
                    _state.value = AuthState.LoggedIn(com.google.gson.Gson().toJson(r.user))
                }
                is com.example.dontforget.data.repository.AuthResult.Failure -> {
                    _state.value = AuthState.Error(r.message)
                }
            }
        }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthState.Idle
    }
}
