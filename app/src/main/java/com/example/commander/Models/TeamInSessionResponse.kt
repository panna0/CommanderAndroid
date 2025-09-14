package com.example.commander.Models

data class TeamInSessionResponse(
    val id: String,
    val team_name: String,
    val players: List<Player>,

)
