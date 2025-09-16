import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.commander.Components.NfcScannerBottomSheet
import com.example.commander.Components.PlayerIconStatus
import com.example.commander.Components.PlayerRow
import com.example.commander.Components.TimerCircle
import com.example.commander.Components.UserList
import com.example.commander.MainActivity
import com.example.commander.Models.Player
import com.example.commander.Models.TeamInSessionResponse
import com.example.commander.Models.asDisplayString
import com.example.commander.Network.ApiContext
import com.example.commander.R
import com.example.commander.findActivity
import java.time.OffsetDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun MatchScreen(navController: NavController, roomCode: String, nfcTagId: State<String?>) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }

    var roomName by remember { mutableStateOf("") }
    var roomGamemode by remember { mutableStateOf("") }
    var roomMaxPlayers by remember { mutableIntStateOf(0) }
    var roomDuration by remember { mutableIntStateOf(0) }
    var myTeam by remember { mutableStateOf<TeamInSessionResponse?>(null) }
    var myTeamName by remember { mutableStateOf("") }
    var myUsername by remember { mutableStateOf<String?>(null) }
    var bombCode by remember { mutableStateOf<String?>(null) }
    var bombDefuseTime by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    var allPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
    val activity = context.findActivity() as? MainActivity ?: return
    val matchStartTime = activity.matchStartTime
    var timeLeft by remember { mutableIntStateOf(0) }


    var allTeams by remember { mutableStateOf<List<TeamInSessionResponse>>(emptyList()) }
    var winner by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf("") }

    var showNfcSheet by remember { mutableStateOf(false) }


    LaunchedEffect(showNfcSheet) {
        activity.enableNfcReader(showNfcSheet)
    }

    DisposableEffect(Unit) {
        onDispose {
            activity.enableNfcReader(false)
        }
    }


    LaunchedEffect(matchStartTime, roomDuration) {
        while (matchStartTime != null && roomDuration > 0) {
            val elapsed =
                OffsetDateTime.now().toEpochSecond() - (matchStartTime?.toEpochSecond() ?: 0L)
            val left = roomDuration * 60 - elapsed
            timeLeft = left.toInt().coerceAtLeast(0)
            if (timeLeft == 0) break
            delay(1000)
        }
    }



    LaunchedEffect(Unit) {
        try {
            val response = apiContext.getConfigurationFromSession(roomCode)
            if (response.isSuccessful) {
                response.body()?.let {
                    roomName = it.configuration_name
                    roomGamemode = it.game_mode_name
                    roomMaxPlayers = it.max_players
                    roomDuration = it.match_duration_minutes
                    bombCode = it.bomb_details.bomb_nfc_code
                    bombDefuseTime = it.bomb_details.defuse_time_seconds

                }
            }
        } catch (e: Exception) {
            Log.e("WaitAdminScreen", "Error fetching room configuration: ${e.message}")
        }
    }




    LaunchedEffect(Unit) {
        try {
            val userResponse = apiContext.getUser()
            if (userResponse.isSuccessful) {
                myUsername = userResponse.body()?.username
            }
        } catch (e: Exception) {
            Log.e("MatchScreen", "Errore getUser: ${e.message}")
        }
    }

    fun updateMyTeam() {
        scope.launch {
            try {
                val teamResponse = apiContext.getTeamsInSession(roomCode)
                if (teamResponse.isSuccessful) {
                    val teams = teamResponse.body() ?: emptyList()
                    allTeams = teams
                    val foundTeam = teams.find { team ->
                        team.players.any { player -> player.username == myUsername }
                    }
                    myTeam = foundTeam
                    // Salva myTeamName solo se non è già valorizzato e il valore trovato non è vuoto
                    if (myTeamName.isEmpty() && foundTeam?.team_name?.isNotEmpty() == true) {
                        myTeamName = foundTeam.team_name
                        Log.d("MatchScreen", "myTeamName salvato: $myTeamName")
                    }
                }
            } catch (e: Exception) {
                Log.e("MatchScreen", "Errore getTeamsInSession: ${e.message}")
            }
        }
    }

    fun getALlPlayer() {
        scope.launch {
            try {
                val response = apiContext.getPlayersInSession(roomCode)
                Log.d("MatchScreen", response.toString())
                if (response.isSuccessful) {
                    val players = response.body() ?: emptyList()
                    Log.d("MatchScreen", players.toString())
                    allPlayers = players

                }
            } catch (e: Exception) {
                Log.e("MatchScreen", "Errore getTeamsInSession: ${e.message}")
            }
        }
    }



    // Unifica l'effetto su myUsername e roomGamemode
    LaunchedEffect(myUsername, roomGamemode) {
        Log.d("MatchScreen", "LaunchedEffect: myUsername=$myUsername, roomGamemode=$roomGamemode")
        if (myUsername != null && roomGamemode.isNotEmpty()) {
            if (roomGamemode == "Free for All") {
                Log.d("MatchScreen", "Chiamo getALlPlayer() in Free for All")
                getALlPlayer()
            } else {
                Log.d("MatchScreen", "Chiamo updateMyTeam() in modalità $roomGamemode")
                updateMyTeam()
            }
        }
    }

    fun reportDeath() {
        scope.launch {
            val status =
                com.example.commander.Models.ChangeStatusRequest(player_status = "Eliminated")
            try {
                val response = apiContext.changeMyStatus(roomCode = roomCode, changeStatusRequest = status)
            } catch (e: Exception) {
                Log.e("MatchScreen", "Errore reportDeath: ${e.message}")
            }
        }
    }

    val sessionSocket = remember(roomCode) {
         com.example.commander.Network.SessionWebSocket(roomCode) { msg ->
            Log.d("SessionWebSocket", "Received message: $msg")
            if (msg.type == "player_status" || msg.type == "player_joined" || msg.type == "player_left") {
                if (myUsername != null) {
                    if (roomGamemode != "Free for All") {
                        updateMyTeam()
                    } else {
                        getALlPlayer()
                    }
                }
            }

             if(msg.type == "session_closed"){
                 activity.endGame()
                 activity.leaveActiveSession()
                 navController.navigate("home") {
                     popUpTo("match/$roomCode") { inclusive = true }
                 }
             }

            if(msg.type == "session_ended"){
                winner = msg.winner?.asDisplayString() ?: ""
                reason = msg.reason ?: "Match Ended"

                val request = com.example.commander.Models.EndSessionRequest(winner = winner, reason = reason)
                scope.launch {
                    try {
                        val response = apiContext.endMatch(roomCode, request)
                        if (response.isSuccessful) {
                            Log.d("MatchScreen", "Session ended successfully")
                            admin = response.body()?.started_by ?: ""
                            winner = response.body()?.winner ?: ""
                            Log.d("MatchScreen", "NAVIGATE: admin=$admin, winner=$winner, myUsername=$myUsername, myTeamName=$myTeamName")
                            if(admin.isNotEmpty() && winner.isNotEmpty() && myUsername != null){
                                val encodedAdmin = java.net.URLEncoder.encode(admin, "UTF-8")
                                val encodedWinner = java.net.URLEncoder.encode(winner, "UTF-8")
                                val encodedMyUsername = java.net.URLEncoder.encode(myUsername, "UTF-8")
                                val encodedRoomGamemode = java.net.URLEncoder.encode(roomGamemode, "UTF-8")
                                val encodedRoomName = java.net.URLEncoder.encode(roomName, "UTF-8")
                                val encodedMyTeamName = java.net.URLEncoder.encode(myTeamName.ifEmpty { "none" }, "UTF-8")
                                navController.navigate("matchResult/${roomCode}/${encodedAdmin}/${encodedWinner}/${encodedMyUsername}/${encodedRoomGamemode}/${encodedRoomName}/${encodedMyTeamName}") {
                                    popUpTo("match/${roomCode}") { inclusive = true }
                                }
                            }
                        } else {
                            Log.e("MatchScreen", "Failed to end session: ${response.code()} - ${response.message()}")
                        }
                    } catch (e: Exception) {
                        Log.e("MatchScreen", "Error ending session: ${e.localizedMessage}")
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

    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AutoSizeText(
                text = roomName,
                maxFontSize = 32.sp,
                minFontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                maxLines = 1
            )
            Spacer(Modifier.height(24.dp))
            if (timeLeft > 0) {
                TimerCircle(
                    millisLeft = timeLeft * 1000L,
                    totalMillis = roomDuration * 60 * 1000L
                )
            }
            Spacer(Modifier.height(80.dp))


            if (myTeam != null) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Your Team", fontSize = 20.sp, fontWeight = FontWeight.Medium)

                    if (myTeam?.players?.any { it.player_status == "Alive" } == true) {
                        val alivePlayers = myTeam?.players?.count { it.player_status == "Alive" } ?: 0
                        Text("Alive - $alivePlayers", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Spacer(Modifier.height(8.dp))
                        UserList(Modifier.fillMaxWidth()) {
                            myTeam?.players?.forEach { player ->
                                if (player.player_status.equals("Alive", ignoreCase = true)) {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() }
                                    )
                                    Spacer(
                                        Modifier.height(1.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    if (myTeam?.players?.any { it.player_status == "Eliminated" } == true) {
                        val eliminatedPlayers =
                            myTeam?.players?.count { it.player_status == "Eliminated" } ?: 0
                        Spacer(Modifier.height(24.dp))
                        Text("Eliminated - $eliminatedPlayers", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Spacer(Modifier.height(8.dp))
                        UserList(Modifier.fillMaxWidth()) {
                            myTeam?.players?.forEach { player ->
                                if (player.player_status == "Eliminated") {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() }
                                    )
                                    Spacer(
                                        Modifier.height(1.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            } else if (allPlayers.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Players", fontSize = 20.sp, fontWeight = FontWeight.Medium)

                    if (allPlayers.any { it.player_status == "Alive" }) {
                        val alivePlayers = allPlayers.count { it.player_status == "Alive" }
                        Spacer(Modifier.height(8.dp))
                        Text("Alive - $alivePlayers", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Spacer(Modifier.height(8.dp))
                        UserList(Modifier.fillMaxWidth()) {
                            allPlayers.forEach { player ->
                                if (player.player_status.equals("Alive", ignoreCase = true)) {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() }
                                    )
                                    Spacer(
                                        Modifier.height(1.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    if (allPlayers.any { it.player_status == "Eliminated" }) {
                        val eliminatedPlayers = allPlayers.count { it.player_status == "Eliminated" }
                        Spacer(Modifier.height(24.dp))
                        Text("Eliminated - $eliminatedPlayers", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Spacer(Modifier.height(8.dp))
                        UserList(Modifier.fillMaxWidth()) {
                            allPlayers.forEach { player ->
                                if (player.player_status == "Eliminated") {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() }
                                    )
                                    Spacer(
                                        Modifier.height(1.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 👇 FAB SEMPRE in basso a destra
        if (
            roomGamemode.replace("\\s+".toRegex(), " ").trim()
                .equals("Bomb Defuse", ignoreCase = true) &&
            myTeamName.trim().equals("Attackers", ignoreCase = true)
        ) {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                onClick = { showNfcSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(64.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.bomb_24px),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                    contentDescription = "Bomb Action",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),

                )
            }
            if (showNfcSheet) {
                NfcScannerBottomSheet(
                    nfcTagId = (context as MainActivity).nfcTagId.value,
                    onDismiss = { showNfcSheet = false
                        activity?.enableNfcReader(false)},
                    bombCode = bombCode,
                    onDefuseSuccess = {
                        winner = myTeamName?: "Attackers"
                        reason = "bomb_defused"
                        val request = com.example.commander.Models.EndSessionRequest(winner = winner, reason = reason)
                        scope.launch {
                            try {
                                val response = apiContext.endMatch(roomCode, request)
                                if (response.isSuccessful) {
                                    Log.d("MatchScreen", "Session ended successfully")
                                } else {
                                    Log.e("MatchScreen", "Failed to end session: ${response.code()} - ${response.message()}")
                                }
                            } catch (e: Exception) {
                                Log.e("MatchScreen", "Error ending session: ${e.localizedMessage}")
                            }
                        }
                    },
                    bombDefuseTimer = bombDefuseTime
                )
            }
        }
    }

}
