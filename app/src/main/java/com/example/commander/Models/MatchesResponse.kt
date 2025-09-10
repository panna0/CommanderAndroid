package com.example.commander.Models

data class MatchesResponse(
    val admin_games : List<Match>,
    val founder_games : List<Match>
)
