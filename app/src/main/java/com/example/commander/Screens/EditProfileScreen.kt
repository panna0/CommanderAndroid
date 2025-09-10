package com.example.commander.Screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.request.ImageRequest
import com.example.commander.Components.CustomInput
import com.example.commander.Components.EditableProfilePhoto
import com.example.commander.Components.MyIconButton
import com.example.commander.Models.CheckUsernameRequest
import com.example.commander.Models.User
import com.example.commander.Network.ApiContext
import com.example.commander.Network.ApiService

import com.example.commander.Network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun EditProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val api: ApiService = remember { RetrofitInstance.getApiService(context) }
    val apiContext = remember { ApiContext(context) }
    val scope = rememberCoroutineScope()

    var usernameError by remember { mutableStateOf<String?>(null) }
    var userData by remember {
        mutableStateOf(
            User(
                first_name = "",
                last_name = "",
                date_of_birth = "",
                email = "",
                password2 = "",
                password = "",
                username = "",
                profile_image = null
            )
        )
    }

    var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var newUsername by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun handleApplyEdit(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                if (newUsername != userData.username) {
                    val response = apiContext.changeUsername(CheckUsernameRequest(newUsername))
                    if (response.isSuccessful) {
                        Log.d("EditProfile", "Username cambiato con successo: $newUsername")
                    } else {
                        Log.e(
                            "EditProfile",
                            "Errore nel cambio username: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("EditProfile", "Errore di rete: ${e.message}")
            } catch (e: Exception) {
                Log.e("EditProfile", "Errore imprevisto: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = apiContext.getUser()
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userData = user
                    newUsername = user.username
                }
            } else {
                Log.e("EditProfile", "API call failed with code: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e("EditProfile", "Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e("EditProfile", "Unexpected error: ${e.message}")
        } finally {
            isLoading = false
        }
    }


    LaunchedEffect(newUsername) {
        if (newUsername != userData.username) {
            if (newUsername.isNotBlank()) {
                delay(500)
                usernameError = apiContext.getUsernameErr(newUsername)
            } else {
                usernameError = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

                Text(text = "Edit Profile", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)

                MyIconButton(
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (!isLoading && usernameError == null) {
                            handleApplyEdit(scope)
                            navController.navigate("profile")
                        }
                    },
                    icon = Icons.Default.Done,
                    contentDescription = "Salva"
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator()
            }

            Box (modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(32.dp))


                    Spacer(modifier = Modifier.height(24.dp))

                    CustomInput(
                        value = newUsername,
                        onValueChange = { username -> newUsername = username },
                        label = "Username",
                        errorMessage = usernameError
                    )
                }


            }

        }
    }
}
