package com.example.commander.Screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.MainActivity
import com.example.commander.Models.Player
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import com.example.commander.R
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
    }catch (e: Exception){
        Log.e("WaitAdminScreen", "Error fetching room configuration: ${e.message}")
    }}


    val activity = context as? MainActivity
    val sessionSocket = remember(roomCode) {
        SessionWebSocket(roomCode) { msg ->
            if (msg.type == "session_started") {
                activity?.matchStartTime = java.time.OffsetDateTime.now()
                CoroutineScope(Dispatchers.Main).launch {
                    navController.navigate("match/$roomCode") {
                        popUpTo("wait_admin_screen/$roomCode") { inclusive = true }
                    }
                }
            } else if (msg.type == "player_joined" || msg.type == "player_left") {

                Log.d("WaitAdminScreen", "Player list changed, fetching updated list")

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




    DisposableEffect(sessionSocket) {
        sessionSocket.connect()
        onDispose {
            sessionSocket.disconnect()

        }
    }

    Box (Modifier.fillMaxSize().padding(16.dp),){
        Column (horizontalAlignment = Alignment.CenterHorizontally){


        Column (Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Text(text = roomName, fontWeight = FontWeight.SemiBold,
                fontSize = 50.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Waiting for admin to start the match...")
        }

        Spacer(modifier = Modifier.height(108.dp))
        Column (Modifier.fillMaxWidth().padding(16.dp)){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(
                        id = getGamemodeicon(
                            roomGamemode
                        )
                    ),
                    contentDescription = "Gamemode Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Gamemode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        roomGamemode,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "players",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Max players",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "$roomMaxPlayers players",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = "clock",
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Durata",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "$roomDuration minuti",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        }
            Spacer(modifier = Modifier.height(108.dp))
            CircularProgressIndicator()

    }}
}
