package com.example.commander.Network

import com.example.commander.Models.CheckEmailRequest
import com.example.commander.Models.CheckEmailResponse
import com.example.commander.Models.CheckUsernameRequest
import com.example.commander.Models.CheckUsernameResponse
import com.example.commander.Models.LoginRequest
import com.example.commander.Models.LoginResponse
import com.example.commander.Models.OtpRequest
import com.example.commander.Models.OtpResponse
import com.example.commander.Models.RegistrationResponse
import com.example.commander.Models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getRequest(@Url url: String): Response<String>

    // Update postRequest to be generic
    @POST
    suspend fun postRequest(@Url url: String, @Body body: Any): Response<LoginResponse>

    // A better approach is to create specific functions for each endpoint.
    // This is cleaner and more explicit.
    @POST("login/")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("login/request-otp")
    suspend fun requestOtp(@Body body: LoginRequest): Response<OtpResponse>

    @POST("login/verify-otp/")
    suspend fun verifyOtp(@Body body: OtpRequest): Response<LoginResponse>

    @POST("register/")
    suspend fun register(@Body body: User): Response<RegistrationResponse>

    @POST("check-username/")
    suspend fun checkUsername(@Body body: CheckUsernameRequest): Response<CheckUsernameResponse>

    @POST("check-email/")
    suspend fun checkEmail(@Body body: CheckEmailRequest): Response<CheckEmailResponse>
}