package com.example.commander

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.commander.Navigation.AppNavigation
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.UI.CommanderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.*

class MainActivity : ComponentActivity() {

    private val apiContext by lazy { ApiContext(this) }
    var currentRoomCode: String? = null
    private var sessionSocket: SessionWebSocket? = null

    var inSession: Boolean = false
    var inGame = mutableStateOf(false)
    var matchStartTime: OffsetDateTime? = null

    private var nfcAdapter: NfcAdapter? = null
    private val _nfcTagId = mutableStateOf<String?>(null)
    val nfcTagId: State<String?> get() = _nfcTagId
    private val mainScope = MainScope()
    private var resetJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "session_updates",
                "Session updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Updates for game sessions" }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        setContent {
            CommanderTheme {
                AppNavigation(nfcTagId = nfcTagId, nfcIntent = null)
            }
        }
    }

    /** === NFC Reader Mode === */
    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        try {
            val id = tag?.id
            val hex = bytesToHex(id)
            Log.d("MainActivity.NFC", "Tag discovered: $hex")

            runOnUiThread {
                _nfcTagId.value = hex


                resetJob?.cancel()
                resetJob = mainScope.launch {
                    delay(1000) // 1 secondo
                    _nfcTagId.value = null
                    Log.d("MainActivity.NFC", "Tag timeout → reset a null")
                }
            }
        } catch (t: Throwable) {
            Log.e("MainActivity.NFC", "ReaderCallback error", t)
        }
    }

    fun enableNfcReader(enable: Boolean) {
        if (nfcAdapter == null) {
            Log.w("MainActivity.NFC", "NFC adapter not available")
            return
        }
        if (enable) {
            val flags =
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            nfcAdapter?.enableReaderMode(this, readerCallback, flags, null)
            Log.d("MainActivity.NFC", "Reader mode ENABLED")
        } else {
            try {
                nfcAdapter?.disableReaderMode(this)
                Log.d("MainActivity.NFC", "Reader mode DISABLED")
            } catch (e: Exception) {
                Log.e("MainActivity.NFC", "Error disabling reader mode", e)
            }
        }
    }

    private fun bytesToHex(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format(Locale.US, "%02X", b))
        }
        return sb.toString()
    }

    override fun onPause() {
        super.onPause()
        try {
            nfcAdapter?.disableReaderMode(this)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        leaveActiveSession()
        try {
            nfcAdapter?.disableReaderMode(this)
        } catch (_: Exception) {}
        super.onDestroy()
    }

    /** === Le tue logiche precedenti (sessioni, API, socket) === */
    fun setActiveSession(roomCode: String, socket: SessionWebSocket) {
        this.currentRoomCode = roomCode
        this.sessionSocket = socket

        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("session_$roomCode")
    }

    suspend fun startMatch(): Boolean {
        currentRoomCode?.let { code ->
            Log.d("MainActivity", "Starting match with code: $code")
            try {
                val response = apiContext.startMatch(code)
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Match started successfully")
                    inGame.value = true
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

    fun endGame() {
        inGame.value = false
    }

    fun leaveActiveSession() {
        currentRoomCode?.let { code ->
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic("session_$code")
            CoroutineScope(Dispatchers.IO).launch {
                apiContext.leaveSession(code)
                sessionSocket?.disconnect()
            }
        }
        currentRoomCode = null
        sessionSocket = null
        inSession = false
        inGame.value = false
    }
}
