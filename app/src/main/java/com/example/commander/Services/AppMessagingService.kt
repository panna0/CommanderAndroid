package com.example.commander.Services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(msg: RemoteMessage) {
        val data = msg.data
        val title = msg.notification?.title ?: "Aggiornamento sessione"
        val body  = msg.notification?.body  ?: data["type"].orEmpty()
    }
}