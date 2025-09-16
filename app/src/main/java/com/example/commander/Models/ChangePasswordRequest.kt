package com.example.commander.Models

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String,
    val new_password2: String
)
