package com.example.commander.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.EditableProfilePhoto
import com.example.commander.Components.MinimalDropdownMenu
import com.example.commander.Components.ProfileList
import com.example.commander.Models.User
import com.example.commander.Network.ApiContext
import com.example.commander.Network.ApiService
import com.example.commander.Network.RetrofitInstance
import com.example.commander.Storage.TokenManager
import com.example.commander.UI.AppShapes
import java.io.IOException

@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }


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

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        try {
            val response = apiContext.getUser()
            Log.d("user", "Login response: ${response.code()} - ${response.message()}")
            Log.d("user", "Body: ${response.body()}")
            Log.d("user", "ErrorBody: ${response.errorBody()?.string()}")
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userData = user
                }
            } else {
                println("API call failed with code: ${response.code()}")
            }
        } catch (e: IOException) {
            println("Network error: ${e.message}")
        } catch (e: Exception) {
            println("An unexpected error occurred: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.End)) {
                Text(text = "My Profile", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(50.dp))
                MinimalDropdownMenu(navController = navController)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    // 5. Usa i dati dinamici per la foto e il nome
                    EditableProfilePhoto(
                        displayName = "${userData.first_name} ${userData.last_name}",
                        enabled = false
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Column {
                        Text(
                            text = "${userData.first_name} ${userData.last_name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "@${userData.username}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Btn(
                            text = "Edit Profile",
                            onClick = {navController.navigate("editProfile")},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = AppShapes.large,
                            small = true,
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            ProfileList(
                birthDate = userData.date_of_birth,
                email = userData.email,
                navController = navController
            )
        }
    }
}