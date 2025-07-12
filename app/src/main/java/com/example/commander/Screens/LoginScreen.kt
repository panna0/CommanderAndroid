package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Models.User
import com.example.commander.UI.AppShapes
import com.example.commander.UI.AppTypography

@Composable
fun LoginScreen(navController: NavHostController, users: List<User>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ){
                Text("Login", style = AppTypography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Write your credentials to login",
                    style = AppTypography.headlineSmall,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            CustomInput(value = username, onValueChange = { username = it }, label = "Username")
            Spacer(modifier = Modifier.height(16.dp))
            CustomInput(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
            Spacer(modifier = Modifier.height(64.dp))

            Btn(
                onClick = {

                },
                text = "Login",
                shape = AppShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(128.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Don't have an account?",
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Sign up", style = AppTypography.bodyMedium)
                }
            }
        }
    }
}
