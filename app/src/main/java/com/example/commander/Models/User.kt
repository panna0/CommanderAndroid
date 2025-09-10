package com.example.commander.Models

import android.net.Uri
import java.io.File

data class User(
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val email: String,
    val password: String,
    val password2: String,
    val username: String,
    var profile_image: File? = null
)
