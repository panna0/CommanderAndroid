package com.example.commander

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

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
