package com.example.commander.Models

data class WebSocketMessage(
    val type: String?,
    val reason : String?,
    val winner : String?,
    val detail : String?,
    val team_id : String?,
    val team_name: String?,
    val start_time: String?,
    val status : String?,
    val player_id: String?,
    val username: String?,

)
