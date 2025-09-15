package com.example.commander.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.Storage.TokenManager
import kotlinx.coroutines.launch

@Composable
fun ProfileList( birthDate: String, email: String, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }


    Column {
        Text(text = "Personal Info", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(16.dp, 0.dp))
        ProfileRow(label = "Birth Date", value = birthDate)

        ProfileRow(label = "Email", value = email)
    }
    Spacer(Modifier.height(24.dp))
    Column {
        Text(text = "Activity", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(16.dp, 0.dp))


        ProfileRow(
            label = null,
            value = "Your Matches",
            isClickable = true,
            leadingIcon = Icons.Default.ControlPoint,
            onClick = {navController.navigate("yourMatches")}
        )

        ProfileRow(
            label = null,
            value = "Log out",
            isClickable = true,
            leadingIcon = Icons.Default.Logout,
            onClick = { scope.launch{
               tokenManager.clearTokens()
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            } }
        )
    }

}