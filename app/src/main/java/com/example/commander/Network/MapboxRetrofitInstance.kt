package com.example.commander.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapboxRetrofitInstance {

    private const val BASE_URL = "https://api.mapbox.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MapboxApiService by lazy {
        retrofit.create(MapboxApiService::class.java)
    }
}