package com.example.commander.Network

import android.util.Log
import com.example.commander.Models.WebSocketMessage
import com.example.commander.Models.Winner
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*

class SessionWebSocket(
    private val sessionCode: String,
    private val onMessage: (WebSocketMessage) -> Unit
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null


    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Winner::class.java, WinnerDeserializer())
        .create()

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
                try {
                    val msg = gson.fromJson(text, WebSocketMessage::class.java)
                    onMessage(msg)
                } catch (e: Exception) {
                    Log.e("WebSocket", "Parsing error: ${e.message}")
                }
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
