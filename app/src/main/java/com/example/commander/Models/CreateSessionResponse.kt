package com.example.commander.Models

data class CreateSessionResponse(
    val id: String,
    val session_status: String,
    val session_room_code: String
)
