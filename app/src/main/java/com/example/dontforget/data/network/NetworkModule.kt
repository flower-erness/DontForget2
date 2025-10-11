package com.example.dontforget.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    // TODO: replace with your deployed server URL (must end with '/')
    private const val BASE_URL = "https://dontforget2-production.up.railway.app/"

    fun provideAuthApi(tokenProvider: () -> String?): AuthApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val authInterceptor = Interceptor { chain ->
            val reqBuilder = chain.request().newBuilder()
            tokenProvider()?.let { token ->
                reqBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(reqBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
