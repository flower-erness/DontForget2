package com.example.dontforget.data.network

data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val user: UserDto, val token: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val user: UserDto, val token: String)
data class UserDto(val id: Long, val name: String, val email: String)
