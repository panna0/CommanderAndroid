package com.example.commander.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commander.Models.Player

@Composable
fun PlayerRow(
    player : Player,
    myUsername: String,
    reportDeath : () -> Unit
) {
    var showDialog = remember { androidx.compose.runtime.mutableStateOf(false) }
    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayerIconStatus(
                nickname = player.username,
                imageUrl = player.profile_image,
                playerStatus = player.player_status,
                size = 48.dp,)
            Spacer(Modifier.width(12.dp))
            Text(text = if(player.username == myUsername){"You"}else{player.username}, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        if (player.username == myUsername && player.player_status == "Alive"){
            Btn(
                text = "Report Death",
                onClick = { showDialog.value = true },
                small = true,
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ))
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Sei sicuro?") },
            text = { Text("Vuoi davvero segnalare la tua morte?") },
            confirmButton = {
                Btn(onClick = {
                    showDialog.value = false
                    reportDeath()
                },
                    text = "Confirm",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                    )

            },
            dismissButton = {
                Btn(onClick = { showDialog.value = false } ,
                    text = "Cancel",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth())
            }
        )
    }

}