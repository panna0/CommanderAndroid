package com.example.commander.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.request.ImageRequest
import com.example.commander.Components.CustomInput
import com.example.commander.Components.EditableProfilePhoto
import com.example.commander.Components.MinimalDropdownMenu
import com.example.commander.Components.MyIconButton

@Composable
fun YourMatchesScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column (modifier = Modifier.fillMaxSize()){
            Row(verticalAlignment = Alignment.CenterVertically) {
                MyIconButton(
                    tint = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate("profile") },
                    icon = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Torna indietro"
                )
                Spacer(modifier = Modifier.width(70.dp))
                Text(text = "Your Matches", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            }
        }



    }

}