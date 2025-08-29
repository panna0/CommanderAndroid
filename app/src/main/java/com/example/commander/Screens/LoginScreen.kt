package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Components.OtpInput
import com.example.commander.Network.AuthContext
import com.example.commander.Models.LoginRequest
import com.example.commander.Models.OtpRequest
import com.example.commander.Models.User
import com.example.commander.Storage.TokenManager
import com.example.commander.UI.AppShapes
import com.example.commander.UI.AppTypography
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavHostController, users: List<User>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Login", style = AppTypography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (otpSent) "Insert the OTP sent to your email" else "Write your credentials to login",
                    style = AppTypography.headlineSmall,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            if (!otpSent) {
                CustomInput(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username"
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true
                )
            } else {
                OtpInput(
                    otpText = otp,
                    onOtpTextChange = { newOtp, isComplete ->
                        otp = newOtp
                    }
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Btn(
                onClick = {
                    scope.launch {

                        isLoading = true
                        try {
                            if (!otpSent) {
                                val response = AuthContext.login(LoginRequest(username, password))
                                val responseOtp = AuthContext.requestOtp(LoginRequest(username, password))
                                if (responseOtp.isSuccessful) {
                                    otpSent = true
                                    error = ""
                                } else {
                                    error = "Login failed"
                                }
                            } else {
                                val response = AuthContext.verifyOtp(OtpRequest(username, otp))
                                if (response.isSuccessful) {
                                    val tokens = response.body()
                                    tokens?.let {
                                        tokenManager.saveTokens(it.access, it.refresh)
                                    }
                                    navController.navigate("home/$username"){
                                        popUpTo("login/") { inclusive = true }
                                    }
                                } else {
                                    error = "OTP non valido"
                                }
                            }
                        } catch (e: Exception) {
                            error = "Errore: ${e.localizedMessage}"
                        }
                        isLoading = false
                    }
                },
                text = if (otpSent) "Verify OTP" else "Login",
                shape = AppShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(128.dp))

            if (!otpSent) {
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
}
