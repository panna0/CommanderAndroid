package com.example.commander.Network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapboxApiService {
    @GET("geocoding/v5/mapbox.places/{query}.json")
    suspend fun getGeocoding(): Response<Any>
}