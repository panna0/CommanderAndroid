package com.example.commander

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.commander.Navigation.AppNavigation
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.UI.CommanderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class MainActivity : ComponentActivity() {

    private val apiContext by lazy { ApiContext(this) }
    var currentRoomCode: String? = null
    private var sessionSocket: SessionWebSocket? = null
    var inSession : Boolean = false
    var inGame by mutableStateOf(false)
    var matchStartTime: OffsetDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommanderTheme {
                AppNavigation()
            }
        }
    }



    override fun onDestroy() {
        leaveActiveSession()
        super.onDestroy()
    }

    fun setActiveSession(roomCode: String, socket: SessionWebSocket) {
        this.currentRoomCode = roomCode
        this.sessionSocket = socket

    }

    suspend fun startMatch(): Boolean {
        currentRoomCode?.let { code ->
            Log.d("MainActivity", "Starting match with code: $code")
            try {
                val response = apiContext.startMatch(code)
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Match started successfully")
                    startGame()
                    return true
                } else {
                    Log.e("MainActivity", "Failed to start match: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error starting match: ${e.localizedMessage}")
            }
        }
        return false
    }

    private fun startGame() {
        inGame = true
    }

    fun endGame() {
        inGame = false
    }

    fun leaveActiveSession() {
        currentRoomCode?.let { code ->
            CoroutineScope(Dispatchers.IO).launch {
                apiContext.leaveSession(code)
                sessionSocket?.disconnect()
            }
        }
        currentRoomCode = null
        sessionSocket = null
        inSession = false
        inGame = false
    }
}
