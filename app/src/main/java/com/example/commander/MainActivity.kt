package com.example.commander

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.commander.Navigation.AppNavigation
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.UI.CommanderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val apiContext by lazy { ApiContext(this) }
    private var currentRoomCode: String? = null
    private var sessionSocket: SessionWebSocket? = null

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

    fun leaveActiveSession() {
        currentRoomCode?.let { code ->
            CoroutineScope(Dispatchers.IO).launch {
                apiContext.leaveSession(code)
                sessionSocket?.disconnect()
            }
        }
        currentRoomCode = null
        sessionSocket = null
    }
}
