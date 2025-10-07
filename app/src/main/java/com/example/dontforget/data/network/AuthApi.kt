package com.example.dontforget.data.network

import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @GET("api/me")
    suspend fun me(@Header("Authorization") authorization: String): Response<RegisterResponse>
}
