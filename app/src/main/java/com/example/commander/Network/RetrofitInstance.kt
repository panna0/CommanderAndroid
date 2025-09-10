package com.example.commander.Network

import android.content.Context
import com.example.commander.Storage.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    @Volatile
    private var INSTANCE: ApiService? = null

    // La funzione di creazione deve accettare il contesto
    fun getApiService(context: Context): ApiService {
        return INSTANCE ?: synchronized(this) {
            val tokenManager = TokenManager(context)
            val authInterceptor = AuthInterceptor(tokenManager)

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://5.189.158.70:8000/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(ApiService::class.java).also { INSTANCE = it }
        }
    }
}