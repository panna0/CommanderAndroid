package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(username: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Benvenuto, $username!", style = MaterialTheme.typography.headlineMedium)
    }
}
