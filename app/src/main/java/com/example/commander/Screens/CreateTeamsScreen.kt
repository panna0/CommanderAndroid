package com.example.commander.Screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import com.example.commander.Components.TeamsCarousel
import com.example.commander.MainActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.commander.Components.Btn
import com.example.commander.Components.IconGrid
import com.example.commander.Components.MyIconButton
import com.example.commander.Models.AddTeamResponse
import com.example.commander.Models.Player
import com.example.commander.Network.ApiContext
import com.example.commander.Network.SessionWebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ContextCastToActivity", "CoroutineCreationDuringComposition")
@Composable
fun CreateTeamsScreen(
    navController: NavController,
    roomCode: String,
    roomName: String?,
    gamemode: String?,
) {
    val context = LocalContext.current
    val activity = context.findActivity() as? MainActivity ?: return
    var showBottomSheet by remember { mutableStateOf(false) }
    var teamView by remember { mutableIntStateOf(0) }
    var players by remember { mutableStateOf(listOf<Player>()) }
    var teams by remember { mutableStateOf(listOf<AddTeamResponse?>()) }
    var assignments by remember { mutableStateOf<Map<String, MutableList<String>>>(emptyMap()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val apiContext = remember { ApiContext(context) }
    val scope = rememberCoroutineScope()

    var roomDuration by remember { mutableIntStateOf(0) }




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






    BackHandler {
        activity.leaveActiveSession()
        navController.popBackStack()
    }

    DisposableEffect(Unit) {
        onDispose { /* non lasciare la sessione qui */ }
    }

    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            assignments = teams.filterNotNull().associate { team ->
                (team.id ?: "") to mutableListOf()
            }
        }
    }

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

    var draggingIcon by remember { mutableStateOf<String?>(null) }

    val assignedIcons = assignments.values.flatten()

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
                                                status = null,
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
                            assignments.forEach { (_, list) ->
                                list.remove(msg.username)
                            }
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
            // NON chiamare activity.leaveActiveSession() qui!
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIconButton(
                    tint = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        activity.leaveActiveSession()
                        navController.popBackStack()
                        navController.popBackStack()
                    },
                    icon = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Torna indietro"
                )
                Text(
                    text = "Create Teams",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(16.dp))
            }
            Spacer(Modifier.height(24.dp))
            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            TeamsCarousel(
                onTeamsLoaded = { loadedTeams -> teams = loadedTeams },
                teams = teams,
                assignments = assignments,
                onDrop = { teamId, username ->
                    assignments = assignments.toMutableMap().apply {
                        val updatedList = (this[teamId] ?: mutableListOf()).toMutableList()
                        updatedList.add(username)
                        this[teamId] = updatedList
                    }
                    draggingIcon = null
                },
                roomCode = roomCode,
                onShowBottomSheet = { index ->
                    showBottomSheet = true
                    teamView = index
                },
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Players",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(8.dp))
            IconGrid(
                icons = players.filter { it.username !in assignedIcons && it.username != draggingIcon },
                onDragStart = { id -> draggingIcon = id },
            )
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
                            Log.d("CreateTeamsScreen", "Start premuto. roomCode: $roomCode, roomDuration: $roomDuration, players: ${players.size}, assignments: $assignments")
                            val allAssigned = players.all { player ->
                                assignments.values.any { it.contains(player.username) }
                            }
                            if (!allAssigned) {
                                errorText = "You must assign all players to a team to start."
                                return@launch
                            } else {
                                errorText = null
                            }

                            kotlinx.coroutines.coroutineScope {
                                assignments.forEach { (teamId, usernames) ->
                                    usernames.forEach { username ->
                                        launch {
                                            try {
                                                val request =
                                                    com.example.commander.Models.AssignPlayerRequest(
                                                        player_username = username,
                                                        team_id = teamId
                                                    )
                                                val response =
                                                    apiContext.assignPlayer(request, roomCode)
                                                if (response.isSuccessful) {
                                                    Log.d(
                                                        "AssignPlayer",
                                                        " $username a $teamId: ${response.code()} - ${response.message()}"
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "AssignPlayer",
                                                    "Exception: ${e.localizedMessage}"
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            val matchStarted = activity.startMatch()
                            if (matchStarted) {
                                Log.d("CreateTeamsScreen", "Navigo verso match/$roomCode")
                                activity.matchStartTime = java.time.OffsetDateTime.now()
                                navController.navigate("match/$roomCode") {
                                    popUpTo("createTeams/$roomCode/$roomName/$gamemode") { inclusive = true }
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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val selectedTeam = teams.getOrNull(teamView)
                val playersInSelectedTeam = assignments[selectedTeam?.id] ?: emptyList()

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "${selectedTeam?.team_name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (selectedTeam?.team_name == "Team 1") {
                            Color(0xFF4260F5)
                        } else {
                            Color(0xFFF54251)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        if (playersInSelectedTeam.isEmpty()) {
                            Text("No players assigned to this team yet.")
                        } else {
                            playersInSelectedTeam.forEach { username ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(20)
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = username,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedTeam?.id?.let { teamId ->
                                                val currentPlayers =
                                                    assignments[teamId]?.toMutableList()
                                                currentPlayers?.remove(username)
                                                assignments = assignments.toMutableMap().apply {
                                                    this[teamId] = currentPlayers ?: mutableListOf()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove player from team"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
