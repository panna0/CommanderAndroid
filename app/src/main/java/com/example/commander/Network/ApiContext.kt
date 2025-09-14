package com.example.commander.Network

import android.content.Context
import android.util.Log
import com.example.commander.Models.*
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

class ApiContext(private val context: Context) {


    private val api: ApiService by lazy {
        RetrofitInstance.getApiService(context)
    }

    suspend fun getUser(): Response<User> {
        val response = api.getUser()
        Log.d("user", "Login response: ${response.code()} - ${response.message()}")
        Log.d("user", "Body: ${response.body()}")
        Log.d("user", "ErrorBody: ${response.errorBody()?.string()}")


        return response
    }

    suspend fun login(credentials: LoginRequest): Response<LoginResponse> {
        val response = api.login(credentials)

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

        return response
    }

    suspend fun requestOtp(loginRequest: LoginRequest): Response<OtpResponse> {
        val response = api.requestOtp(loginRequest)

        Log.d("otp", "otp response: ${response.code()} - ${response.message()}")

        return response
    }

    suspend fun register(user: User): Response<RegistrationResponse> {
        val response = api.register(user)

        Log.d("AuthContext", "Login response: ${response.code()} - ${response.message()}")
        Log.d("AuthContext", "Body: ${response.body()}")
        Log.d("AuthContext", "ErrorBody: ${response.errorBody()?.string()}")

        return response
    }

    suspend fun checkUsername(checkUsernameRequest: CheckUsernameRequest): Response<CheckUsernameResponse> {
        return api.checkUsername(checkUsernameRequest)
    }

    suspend fun checkEmail(checkEmailRequest: CheckEmailRequest): Response<CheckEmailResponse> {
        return api.checkEmail(checkEmailRequest)
    }

    suspend fun refreshToken(refreshTokenRequest: refreshTokenRequest): Response<refreshTokenResponse> {
        val response = api.refreshToken(refreshTokenRequest)
        Log.d("refresh", "refresh response: ${response.code()} - ${response.message()}")
        Log.d("refresh", "Body: ${response.body()}")
        Log.d("refresh", "ErrorBody: ${response.errorBody()?.string()}")

        return response
    }

    suspend fun getUsernameErr(username: String): String? {
        if (username.isBlank() || username.contains(" ")) {
            return "Lo username non può essere vuoto o contenere spazi."
        }

        return try {
            val response = checkUsername(CheckUsernameRequest(username))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.username_taken) {
                    "Username già in utilizzo."
                } else {
                    null
                }
            } else {
                "Errore di connessione al server."
            }
        } catch (e: Exception) {
            "Errore di connessione al server."
        }
    }

    suspend fun changeUsername(checkUsernameRequest: CheckUsernameRequest): Response<OtpResponse> {
        val response = api.changeUsername(checkUsernameRequest)

        return response
    }

    suspend fun getMatches(): Response<MatchesResponse> {
        val response = api.getMatches()

        return response
    }

    suspend fun createSession(id: String): Response<CreateSessionResponse> {
        val response = api.createSession(id)

        return response
    }

    suspend fun leaveSession(roomCode: String): Response<OtpResponse> {
        val response = api.leaveSession(roomCode)

        return response
    }

    suspend fun getMatch(id: String): Response<Match> {
        val response = api.getMatch(id)

        return response
    }

    suspend fun addTeam(roomCode: String, addTeamRequest: AddTeamRequest): Response<AddTeamResponse>{
        val response = api.createTeams(body = addTeamRequest, roomCode)

        Log.d("add team", "Body: ${response.body()}")

        return response
    }

    suspend fun getPlayersInSession(roomCode: String): Response<List<Player>>{
        val response = api.getPlayersInSession(roomCode = roomCode)

        return response
    }

    suspend fun getPlayerPic (username: String): Response<PlayerPic>{
        val response = api.getPlayerPic(username)

        return response
    }
    suspend fun assignPlayer(assignPlayerRequest: AssignPlayerRequest, roomCode: String): Response<AssignPlayerResponse>{
        val response = api.assignPlayerToTeam(assignPlayerRequest, roomCode)

        return response
    }

    suspend fun startMatch(roomCode: String): Response<OtpResponse>{
        val response = api.startMatch(roomCode)
        Log.d("start match", "Body: ${response}")
        return response
    }

    suspend fun joinSession(joinSessionRequest: JoinSessionRequest): Response<OtpResponse>{
        val response = api.joinSession(joinSessionRequest)
        Log.d("join session", "Body: ${response}")
        return response
    }

    suspend fun getConfigurationFromSession(roomCode: String): Response<Match>{
        val response = api.getConfigurationFromSession(roomCode)
        Log.d("get config", "Body: ${response}")
        return response
    }

    suspend fun getTeamsInSession(roomCode: String): Response<List<TeamInSessionResponse>>{
        val response = api.getTeamsFromSession(roomCode)
        Log.d("get teams", "Body: ${response}")
        return response
    }

    suspend fun changeMyStatus(changeStatusRequest: ChangeStatusRequest, roomCode: String): Response<OtpResponse> {
        // Recupera il token attuale
        val tokenManager = com.example.commander.Storage.TokenManager(context)
        val accessToken = kotlinx.coroutines.runBlocking { tokenManager.accessToken.firstOrNull() }
        Log.d("change status", "Token usato: $accessToken")
        Log.d("change status", "Body inviato: $changeStatusRequest")
        val response = api.changeMyStatus(roomCode = roomCode, body = changeStatusRequest)
        Log.d("change status", "Response code: ${response.code()} - ${response.message()}")
        Log.d("change status", "ErrorBody: ${response.errorBody()?.string()}")
        return response
    }
}