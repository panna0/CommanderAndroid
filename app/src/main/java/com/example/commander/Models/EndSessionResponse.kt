package com.example.commander.Models

data class EndSessionResponse(
    val detail: String,
    val reason: String,
    val winner: String,
    val started_by: String,
)
