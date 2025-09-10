package com.example.commander.Network

import okhttp3.Interceptor
import okhttp3.Response
import com.example.commander.Storage.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val accessToken = runBlocking {
            tokenManager.accessToken.firstOrNull()
        }

        val newRequest = originalRequest.newBuilder()
        if (accessToken != null) {
            newRequest.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(newRequest.build())
    }
}