package com.example.commander.Models

import android.net.Uri

data class User(
    val name: String,
    val surname: String,
    val birthdate: String,
    val email: String,
    val password: String,
    val username: String,
    val profilePhotoUri: Uri? = null
)
