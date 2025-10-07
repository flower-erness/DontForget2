package com.example.dontforget.data.repository

import android.content.Context
import com.example.dontforget.data.network.*
import com.example.dontforget.data.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson

sealed interface AuthResult {
    data class Success(val user: UserDto, val token: String) : AuthResult
    data class Failure(val message: String) : AuthResult
}

class AuthRepository(context: Context) {
    private val storage = SecureStorage(context.applicationContext)
    private val api = NetworkModule.provideAuthApi { storage.getToken() }

    suspend fun register(name: String, email: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            val resp = api.register(RegisterRequest(name, email, password))
            if (resp.isSuccessful) {
                val body = resp.body()!!
                storage.saveToken(body.token)
                storage.saveUserJson(Gson().toJson(body.user))
                AuthResult.Success(body.user, body.token)
            } else {
                val err = resp.errorBody()?.string() ?: resp.message()
                AuthResult.Failure(err)
            }
        }

    suspend fun login(email: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            val resp = api.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                val body = resp.body()!!
                storage.saveToken(body.token)
                storage.saveUserJson(Gson().toJson(body.user))
                AuthResult.Success(body.user, body.token)
            } else {
                val err = resp.errorBody()?.string() ?: resp.message()
                AuthResult.Failure(err)
            }
        }

    fun logout() {
        storage.clearToken()
    }

    fun getToken(): String? = storage.getToken()
}
