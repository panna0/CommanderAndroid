package com.example.commander.Network

import android.util.Log
import okhttp3.*

class SessionWebSocket(
    private val sessionCode: String,
    private val onMessage: (String) -> Unit
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("ws://5.189.158.70:8000/ws/session/$sessionCode/")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected to session $sessionCode")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("WebSocket", "Message: $text")
                onMessage(text)
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $reason")
                ws.close(1000, null)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }
        })
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnected")
    }
}
