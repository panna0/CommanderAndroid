package com.example.commander.Network

import com.example.commander.Models.AddTeamRequest
import com.example.commander.Models.AddTeamResponse
import com.example.commander.Models.AssignPlayerRequest
import com.example.commander.Models.AssignPlayerResponse
import com.example.commander.Models.CheckEmailRequest
import com.example.commander.Models.CheckEmailResponse
import com.example.commander.Models.CheckUsernameRequest
import com.example.commander.Models.CheckUsernameResponse
import com.example.commander.Models.CreateSessionResponse
import com.example.commander.Models.JoinSessionRequest
import com.example.commander.Models.LoginRequest
import com.example.commander.Models.LoginResponse
import com.example.commander.Models.Match
import com.example.commander.Models.MatchesResponse
import com.example.commander.Models.OtpRequest
import com.example.commander.Models.OtpResponse
import com.example.commander.Models.Player
import com.example.commander.Models.PlayerPic
import com.example.commander.Models.RegistrationResponse
import com.example.commander.Models.User
import com.example.commander.Models.refreshTokenRequest
import com.example.commander.Models.refreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getRequest(@Url url: String): Response<String>

    // Update postRequest to be generic
    @POST
    suspend fun postRequest(@Url url: String, @Body body: Any): Response<LoginResponse>



    @GET("current-user/")
    suspend fun getUser(): Response<User>

    @GET("my-game-configurations/")
    suspend fun getMatches(): Response<MatchesResponse>

    @GET("game-configurations/{id}/")
    suspend fun getMatch(
        @Path("id") gameId: String
    ): Response<Match>

    @GET("sessions/{roomCode}/players/")
    suspend fun getPlayersInSession(
        @Path("roomCode") roomCode: String
    ): Response<List<Player>>

    @GET("user/{username}/profile-image/")
    suspend fun getPlayerPic(
        @Path("username") username : String
    ): Response<PlayerPic>

    @GET("sessions/{roomCode}/configuration/")
    suspend fun getConfigurationFromSession(
        @Path("roomCode") roomCode: String
    ): Response<Match>

    @POST("login/")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("login/request-otp/")
    suspend fun requestOtp(@Body body: LoginRequest): Response<OtpResponse>

    @POST("login/verify-otp/")
    suspend fun verifyOtp(@Body body: OtpRequest): Response<LoginResponse>

    @POST("register/")
    suspend fun register(@Body body: User): Response<RegistrationResponse>

    @POST("check-username/")
    suspend fun checkUsername(@Body body: CheckUsernameRequest): Response<CheckUsernameResponse>

    @POST("check-email/")
    suspend fun checkEmail(@Body body: CheckEmailRequest): Response<CheckEmailResponse>

    @POST("token/refresh/")
    suspend fun refreshToken(@Body body: refreshTokenRequest): Response<refreshTokenResponse>

    @POST("change-username/")
    suspend fun changeUsername(@Body body: CheckUsernameRequest): Response<OtpResponse>

    @POST("game-configurations/{id}/create-session/")
    suspend fun createSession(
        @Path("id") gameId: String
    ): Response<CreateSessionResponse>

    @POST("sessions/{roomCode}/leave/")
    suspend fun leaveSession(
        @Path("roomCode") roomCode: String
    ): Response<OtpResponse>

    @POST("sessions/{roomCode}/teams/create/")
    suspend fun createTeams(@Body body: AddTeamRequest,
        @Path("roomCode") roomCode: String
    ): Response<AddTeamResponse>

    @POST("sessions/{roomCode}/assign-player/")
    suspend fun assignPlayerToTeam(
        @Body body: AssignPlayerRequest,
        @Path("roomCode") roomCode: String
    ): Response<AssignPlayerResponse>

    @POST("sessions/{roomCode}/start/")
    suspend fun startMatch(
        @Path("roomCode") roomCode: String
    ): Response<OtpResponse>


    @POST("join-session/")
    suspend fun joinSession(@Body body: JoinSessionRequest): Response<OtpResponse>



}