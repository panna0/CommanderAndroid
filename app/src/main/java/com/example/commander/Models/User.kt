package com.example.commander.Models

import android.net.Uri

data class User(
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val email: String,
    val password: String,
    val password2: String,
    val username: String,
    val profile_image: Uri? = null
)
