package com.example.commander.Network

import android.util.Log
import com.example.commander.Models.*
import retrofit2.Response

object AuthContext {
    private val api = RetrofitInstance.api

    suspend fun login(credentials: LoginRequest): Response<LoginResponse> {
        // Call the new, specific login function on the ApiService
        val response = api.login(credentials)

        // Logging for debugging
        Log.d("AuthContext", "Login response: ${response.code()} - ${response.message()}")
        Log.d("AuthContext", "Body: ${response.body()}")
        Log.d("AuthContext", "ErrorBody: ${response.errorBody()?.string()}")

        return response
    }

    suspend fun verifyOtp(otpRequest: OtpRequest): Response<LoginResponse> {
        val response = api.verifyOtp(otpRequest)

        Log.d("otp", "otp response: ${response.code()} - ${response.message()}")
        Log.d("otp", "Body: ${response.body()}")
        Log.d("otp", "ErrorBody: ${response.errorBody()?.string()}")

        // Call the specific verifyOtp function
        return response
    }

    suspend fun requestOtp(loginRequest: LoginRequest): Response<OtpResponse>{
        val response = api.requestOtp(loginRequest)

        Log.d("otp", "otp response: ${response.code()} - ${response.message()}")




        return response
    }

    suspend fun register(user: User): Response<RegistrationResponse>{

        val response = api.register(user)

        Log.d("AuthContext", "Login response: ${response.code()} - ${response.message()}")
        Log.d("AuthContext", "Body: ${response.body()}")
        Log.d("AuthContext", "ErrorBody: ${response.errorBody()?.string()}")

        return response
    }

    suspend fun checkUsername(checkUsernameRequest: CheckUsernameRequest): Response<CheckUsernameResponse>{

        return api.checkUsername(checkUsernameRequest)
    }

    suspend fun checkEmail(checkEmailRequest: CheckEmailRequest) : Response<CheckEmailResponse>{
        return api.checkEmail(checkEmailRequest)
    }

    suspend fun refreshToken(refreshTokenRequest: refreshTokenRequest) : Response<refreshTokenResponse>{

        return api.refreshToken(refreshTokenRequest)
    }

    // The helper functions are no longer needed, so you can remove them!
    // private inline fun <reified T> Response<String>.toTypedResponse(): Response<T> { ... }
    // private fun Response<String>.toUnitResponse(): Response<Unit> { ... }
}