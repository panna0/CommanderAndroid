package com.example.commander.Screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Models.User
import com.example.commander.UI.AppTypography

@Composable
fun RegisterScreen(
    navController: NavHostController,
    users: MutableList<User>,
    onRegister: (User) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf("") }

    val userData = remember {
        mutableStateOf(
            User(
                name = "",
                surname = "",
                birthdate = "",
                email = "",
                password = "",
                username = "",
                profilePhotoUri = null
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ){
            Text("Sign Up", style = AppTypography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Write your credentials to login",
                style = AppTypography.headlineSmall,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        Spacer(modifier = Modifier.height(64.dp))

        when (step) {
            0 -> {
                CustomInput(
                    value = userData.value.name,
                    onValueChange = { userData.value = userData.value.copy(name = it) },
                    label = "Nome"
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.surname,
                    onValueChange = { userData.value = userData.value.copy(surname = it) },
                    label = "Cognome"
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.birthdate,
                    onValueChange = { userData.value = userData.value.copy(birthdate = it) },
                    label = "Data di nascita",
                    isDate = true,
                )
            }

            1 -> {
                CustomInput(
                    value = userData.value.email,
                    onValueChange = { userData.value = userData.value.copy(email = it) },
                    label = "Email"
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.password,
                    onValueChange = { userData.value = userData.value.copy(password = it) },
                    label = "Password",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = "",
                    onValueChange = {
                        error = if (it != userData.value.password) "Le password non coincidono" else ""
                    },
                    label = "Ripeti Password",
                    isPassword = true
                )
            }

            2 -> {
                CustomInput(
                    value = userData.value.username,
                    onValueChange = { userData.value = userData.value.copy(username = it) },
                    label = "Username"
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Foto profilo (simulata)")

            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                Btn(
                    onClick = { step-- }, text = "Back", modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant))
                Spacer(modifier = Modifier.width(8.dp))
            }

            Btn(
                onClick = {
                    if (step < 2) {
                        step++
                    } else {

                        if (
                            userData.value.name.isNotBlank() &&
                            userData.value.surname.isNotBlank() &&
                            userData.value.email.isNotBlank() &&
                            userData.value.password.isNotBlank() &&
                            userData.value.username.isNotBlank()
                        ) {
                            onRegister(userData.value)
                            users.add(userData.value)
                            navController.navigate("login")
                        } else {
                            error = "Compila tutti i campi"
                        }
                    }
                },
                text = if (step < 2) "Next" else "Sign Up",
                modifier = Modifier.weight(1f)
            )
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(100.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Already have an account?",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Sign in", style = AppTypography.bodyMedium)
            }
        }
    }
}
