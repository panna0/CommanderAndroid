package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.commander.Components.CustomInput
import com.example.commander.Components.MyIconButton
import com.example.commander.Network.ApiContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val apiContext = remember { ApiContext(navController.context) }

    fun getPasswordError(password: String): String? {
        if (password.length < 8) return "La password deve contenere almeno 8 caratteri."
        if (!password.any { it.isLowerCase() }) return "La password deve contenere almeno una minuscola."
        if (!password.any { it.isUpperCase() }) return "La password deve contenere almeno una maiuscola."
        if (!password.any { it.isDigit() }) return "La password deve contenere almeno un numero."
        if (!password.any { "!@#&()–[{}]:;',?/*~$^+=<>".contains(it) }) return "La password deve contenere un carattere speciale."
        return null
    }

    fun handleChangePassword() {
        showValidationErrors = true
        if (getPasswordError(newPassword) == null && newPassword == confirmPassword) {
            scope.launch {
                try {
                    isLoading = true
                    val request = com.example.commander.Models.ChangePasswordRequest(
                        oldPassword,
                        newPassword,
                        confirmPassword
                    )
                    val response = apiContext.changePassword(request)
                    if (response.isSuccessful) {
                        showSuccessDialog = true
                    } else {
                        errorMessage = "Errore: ${response.code()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Errore. Riprova più tardi."
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
        ) {
            // Barra superiore custom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIconButton(
                    tint = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate("profile") },
                    icon = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Torna indietro"
                )

                Text(
                    text = "Change Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )

                MyIconButton(
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (!isLoading) {
                            handleChangePassword()
                        }
                    },
                    icon = Icons.Default.Done,
                    contentDescription = "Conferma"
                )
            }

            // Contenuto centrato
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomInput(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = "Vecchia Password",
                        isPassword = true
                    )

                    CustomInput(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "Nuova Password",
                        isPassword = true,
                        errorMessage = if (showValidationErrors) getPasswordError(newPassword) else null
                    )

                    CustomInput(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Conferma Password",
                        isPassword = true,
                        errorMessage = if (showValidationErrors && newPassword != confirmPassword)
                            "Le password non coincidono."
                        else null
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Dialog successo
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    navController.navigate("profile") {
                        popUpTo("changePassword") { inclusive = true }
                    }
                },
                title = { Text("Successo") },
                text = { Text("Password cambiata con successo!") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        navController.navigate("profile") {
                            popUpTo("changePassword") { inclusive = true }
                        }
                    }) {
                        Text("Continua")
                    }
                }
            )
        }
    }
}
