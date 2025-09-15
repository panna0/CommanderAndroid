package com.example.commander.Screens

import AutoSizeText
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.commander.Components.Btn
import com.example.commander.Components.MyIconButton
import com.example.commander.Components.PlayerRow
import com.example.commander.Components.UserList
import com.example.commander.MainActivity
import com.example.commander.Models.Player
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.findActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WaitingPlayerScreen(
    navController: NavController,
    roomCode: String,
    roomName: String?,
    gamemode: String?,
)  {
    val context = LocalContext.current
    val activity = context.findActivity() as? MainActivity ?: return
    val apiContext = remember { ApiContext(context) }
    val scope = rememberCoroutineScope()

    var roomDuration by remember { mutableIntStateOf(0) }
    var players by remember { mutableStateOf(listOf<Player>()) }
    var errorText by remember { mutableStateOf<String?>(null) }


    BackHandler {
        activity.leaveActiveSession()
        navController.navigate("home") {
            popUpTo("waitingPlayers/$roomCode/$roomName/$gamemode") { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = apiContext.getConfigurationFromSession(roomCode)
            if (response.isSuccessful) {
                response.body()?.let {
                    roomDuration = it.match_duration_minutes
                }
            }
        }catch (e: Exception){
            Log.e("WaitAdminScreen", "Error fetching room configuration: ${e.message}")
        }}



    LaunchedEffect(Unit) {
        val newPlayers = mutableListOf<Player>()
        try {
            val response = apiContext.getPlayersInSession(roomCode)
            if (response.isSuccessful) {
                response.body()?.let { newPlayers.addAll(it) }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e("getSessionPlayers", it) }
        }
        players = newPlayers
    }

    val sessionSocket = remember(roomCode) {
        SessionWebSocket(roomCode) { msg ->
            scope.launch(Dispatchers.Main) {
                when (msg.type) {
                    "player_joined" -> {
                        if (msg.username != null) {
                            if (players.none { it.username == msg.username }) {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val response = apiContext.getPlayerPic(msg.username)
                                        Log.d("pic", "chiamata riuscita")
                                        if (response.isSuccessful) {
                                            val newPlayer = Player(
                                                id = msg.player_id,
                                                username = msg.username,
                                                player_status = null,
                                                profile_image = response.body()?.profile_image
                                            )
                                            scope.launch(Dispatchers.Main) {
                                                players = players + newPlayer
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("player_joined", "Error fetching player pic: ${e.localizedMessage}")
                                    }
                                }
                            }
                        }
                    }
                    "player_left" -> {
                        if (msg.username != null) {
                            players = players.filter { it.username != msg.username }

                        }
                    }
                }
            }
        }
    }

    DisposableEffect(sessionSocket) {
        sessionSocket.connect()
        onDispose {
            sessionSocket.disconnect()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column (Modifier.fillMaxSize().padding(16.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIconButton(
                    tint = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        activity.leaveActiveSession()
                        navController.navigate("home") {
                            popUpTo("waitingPlayers/$roomCode/$roomName/$gamemode") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    icon = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Torna indietro"
                )
                Text(
                    text = "Waiting For Players",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            AutoSizeText(
                text = roomName?:"",
                maxFontSize = 32.sp,
                minFontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Players - ${players.size}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            UserList (maxHeight = 500.dp){
                players.forEach { player ->
                    PlayerRow(
                        player = player,
                        myUsername = players[0].username,
                        reportDeath = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.outline)
                .height(104.dp),
        ) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = roomName.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = gamemode.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Btn(text = "Start", onClick = {
                    scope.launch {
                        try {
                            val matchStarted = activity.startMatch()
                            if (matchStarted) {
                                Log.d("CreateTeamsScreen", "Navigo verso match/$roomCode")
                                activity.matchStartTime = java.time.OffsetDateTime.now()
                                navController.navigate("match/$roomCode") {
                                    popUpTo("waitingPlayers/$roomCode/$roomName/$gamemode") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                errorText = "Impossibile avviare la partita."
                            }
                        } catch (e: Exception) {
                            errorText = "Crash in start: ${e.localizedMessage}"
                            Log.e("CreateTeamsScreen", "Crash in start: ${e.localizedMessage}")
                        }
                    }
                })
            }
        }

    }


}