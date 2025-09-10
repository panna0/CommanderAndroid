package com.example.commander.Models

data class Match(
    val id : String,
    val configuration_name : String,
    val configuration_description : String,
    val max_players : Int,
    val game_mode_name : String,
    val match_duration_minutes : Int,
    val bomb_details : BombDetail,

)
