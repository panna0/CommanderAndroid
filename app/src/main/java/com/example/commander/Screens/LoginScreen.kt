package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.UI.AppShapes

@Composable
fun LoginScreen(navController: NavHostController, users: List<Pair<String, String>>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        CustomInput(value = username, onValueChange = { username = it }, label = "Username")
        CustomInput(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

        Spacer(modifier = Modifier.height(8.dp))
        Btn(onClick = {
            if (users.any { it.first == username && it.second == password }) {
                navController.navigate("home/$username")
            } else {
                error = "Credenziali errate"
            }},
            text = "Login",
            shape = AppShapes.medium,
            modifier = Modifier.fillMaxWidth()

        )

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Non hai un account? Registrati")
        }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}


