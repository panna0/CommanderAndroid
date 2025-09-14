import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.commander.Components.PlayerIconStatus
import com.example.commander.Components.PlayerRow
import com.example.commander.Components.TimerCircle
import com.example.commander.Components.UserList
import com.example.commander.MainActivity
import com.example.commander.Models.Player
import com.example.commander.Models.TeamInSessionResponse
import com.example.commander.Network.ApiContext
import com.example.commander.findActivity
import java.time.OffsetDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun MatchScreen(navController: NavController, roomCode: String) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }

    var roomName by remember { mutableStateOf("") }
    var roomGamemode by remember { mutableStateOf("") }
    var roomMaxPlayers by remember { mutableIntStateOf(0) }
    var roomDuration by remember { mutableIntStateOf(0) }
    var myTeam by remember { mutableStateOf<TeamInSessionResponse?>(null) }
    var myUsername by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var allPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
    val activity = context.findActivity() as? MainActivity ?: return
    val matchStartTime = activity.matchStartTime
    var timeLeft by remember { mutableIntStateOf(0) }

    var allTeams by remember { mutableStateOf<List<TeamInSessionResponse>>(emptyList()) }
    var winner by remember { mutableStateOf<List<Player>>(emptyList()) }


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
                    myTeam = teams.find { team ->
                        team.players.any { player -> player.username == myUsername }
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
                if (response.isSuccessful) {
                    val players = response.body() ?: emptyList()
                    allPlayers = players
                }
            } catch (e: Exception) {
                Log.e("MatchScreen", "Errore getTeamsInSession: ${e.message}")
            }
        }
    }



    LaunchedEffect(myUsername) {
        if (myUsername != null) {
            if (roomGamemode != "Free for All") {
                updateMyTeam()
            } else {
                getALlPlayer()
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
            if(msg.type == "session_ended"){
                when(roomGamemode){
                    "Free for All" ->{
                        val winningPlayers = allPlayers.filter { it.player_status == "Alive" }
                        winner = winningPlayers
                    }
                    "Bomb Defuse" ->{
                        val winningTeam = allTeams.find { it.team_name == msg.winner }
                        winner = winningTeam?.players ?: emptyList()
                    }
                    "Team Deathmatch" ->{
                        val alivePlayersTeam1 = allTeams.getOrNull(0)?.players?.filter { it.player_status == "Alive" } ?: emptyList()
                        val alivePlayersTeam2 = allTeams.getOrNull(1)?.players?.filter { it.player_status == "Alive" } ?: emptyList()
                        winner = if(alivePlayersTeam1.size > alivePlayersTeam2.size) allTeams[0].players else if(alivePlayersTeam2.size > alivePlayersTeam1.size) allTeams[1].players else emptyList()
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

    Box(Modifier.fillMaxSize().padding(24.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = roomName, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
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
                        val alivePlayers =
                            myTeam?.players?.count { it.player_status == "Alive" } ?: 0
                        Text(
                            text = "Alive - $alivePlayers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(8.dp))

                        UserList(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            myTeam?.players?.forEach { player ->
                                if (player.player_status?.equals(
                                        "Alive",
                                        ignoreCase = true
                                    ) == true
                                ) {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() })
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
                        Text(
                            text = "Eliminated - $eliminatedPlayers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(8.dp))
                        UserList(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            myTeam?.players?.forEach { player ->
                                if (player.player_status == "Eliminated") {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() })
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
                        Text(
                            text = "Alive - $alivePlayers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(8.dp))

                        UserList(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allPlayers.forEach { player ->
                                if (player.player_status?.equals(
                                        "Alive",
                                        ignoreCase = true
                                    ) == true
                                ) {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() })
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
                        Text(
                            text = "Eliminated - $eliminatedPlayers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(8.dp))
                        UserList(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allPlayers.forEach { player ->
                                if (player.player_status == "Eliminated") {
                                    PlayerRow(
                                        player = player,
                                        myUsername = myUsername ?: "",
                                        reportDeath = { reportDeath() })
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
}
}
