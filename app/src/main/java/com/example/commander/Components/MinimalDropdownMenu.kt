package com.example.commander.Components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.commander.Network.ApiContext
import kotlinx.coroutines.launch

@Composable
fun MinimalDropdownMenu(
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }
    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .padding(16.dp),

    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.Settings, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Change Password") },
                onClick = {
                    expanded = false
                    navController.navigate("changePassword")
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Account") },
                onClick = { scope.launch {
                    try{
                        val response = apiContext.deleteAccount()
                        navController.navigate("home"){
                            popUpTo("profile") { inclusive = true }
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }}
            )
        }
    }
}