package com.example.commander.Models


sealed interface Winner {
    data class Single(val value: String) : Winner
    data class Players(val list: List<Player>) : Winner
    data class Teams(val list: List<Team>) : Winner
}
fun Winner.asDisplayString(): String = when (this) {
    is Winner.Single -> value
    is Winner.Players -> list.joinToString(" ") { it.username }
    is Winner.Teams -> list.joinToString(" ") { it.team_name }
}


data class WebSocketMessage(
    val type: String?,
    val reason: String?,
    val winner: Winner?,
    val detail: String?,
    val team_id: String?,
    val team_name: String?,
    val start_time: String?,
    val status: String?,
    val player_id: String?,
    val username: String?,
)
