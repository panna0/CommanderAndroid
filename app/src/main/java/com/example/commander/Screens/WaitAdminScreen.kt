package com.example.commander.Screens

import AutoSizeText
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.Components.MyIconButton
import com.example.commander.MainActivity
import com.example.commander.Models.Player
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.R
import com.example.commander.findActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WaitAdminScreen(navController: NavHostController, roomCode: String) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }

    var roomName by remember { mutableStateOf("") }
    var roomGamemode by remember { mutableStateOf("") }
    var roomMaxPlayers by remember { mutableIntStateOf(0) }
    var roomDuration by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val response = apiContext.getConfigurationFromSession(roomCode)
            if (response.isSuccessful) {
                response.body()?.let {
                    roomName = it.configuration_name
                    roomGamemode = it.game_mode_name
                    roomMaxPlayers = it.max_players
                    roomDuration = it.match_duration_minutes
                }
            }
        } catch (e: Exception) {
            Log.e("WaitAdminScreen", "Error fetching room configuration: ${e.message}")
        }
    }

    val activity = context.findActivity() as? MainActivity
    val sessionSocket = remember(roomCode) {
        SessionWebSocket(roomCode) { msg ->
            if (msg.type == "session_started") {
                activity?.matchStartTime = java.time.OffsetDateTime.now()
                CoroutineScope(Dispatchers.Main).launch {
                    navController.navigate("match/$roomCode") {
                        popUpTo("wait_admin_screen/$roomCode") { inclusive = true }
                    }
                }
            }
        }
    }

    fun getGamemodeicon(gamemode: String): Int {
        return when (gamemode) {
            "Bomb Defuse" -> R.drawable.bomb_24px
            "Team Deathmatch" -> R.drawable.skull_24px
            "Free for All" -> R.drawable.swords_24px
            else -> R.drawable.ic_launcher_foreground
        }
    }

    BackHandler {
        activity?.leaveActiveSession()
        navController.navigate("home") {
            popUpTo("wait_admin_screen/$roomCode") { inclusive = true }
        }
    }

    DisposableEffect(sessionSocket) {
        sessionSocket.connect()
        onDispose { sessionSocket.disconnect() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxHeight(). fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIconButton(
                    tint = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        activity?.leaveActiveSession()
                        navController.navigate("home") {
                            popUpTo("wait_admin_screen/$roomCode") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    icon = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Torna indietro"
                )
                Text(
                    text = "Waiting For Admin",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(16.dp))
            }
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                // Card centrata
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AutoSizeText(
                            text = roomName,
                            maxFontSize = 32.sp,
                            minFontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Waiting for admin to start the match...",
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Gamemode row
                        InfoRow(
                            icon = {
                                Image(
                                    painter = painterResource(id = getGamemodeicon(roomGamemode)),
                                    contentDescription = "Gamemode Icon",
                                    modifier = Modifier.size(40.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                                )
                            },
                            label = "Gamemode",
                            value = roomGamemode
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Max players row
                        InfoRow(
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "players",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            label = "Max players",
                            value = "$roomMaxPlayers players"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Duration row
                        InfoRow(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "clock",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            label = "Duration",
                            value = "$roomDuration minutes"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                // Fuori dalla card
                CircularProgressIndicator()
            }
        }
        }

}

@Composable
fun InfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
