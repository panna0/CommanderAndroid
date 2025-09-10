import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Components.ScrollableRowsLazy
import com.example.commander.MainActivity
import com.example.commander.Storage.TokenManager
import com.example.commander.Network.ApiContext
import com.example.commander.Models.refreshTokenRequest
import com.example.commander.Network.ApiService
import com.example.commander.Network.RetrofitInstance
import com.example.commander.Network.SessionWebSocket
import com.example.commander.R
import com.example.commander.UI.AppShapes
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(username: String, navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var roomCode by remember { mutableStateOf("") }
    var roomCode2 by remember { mutableStateOf("") }
    val api: ApiService = remember { RetrofitInstance.getApiService(context) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val apiContext = remember { ApiContext(context) }

    var roomName by remember { mutableStateOf("") }
    var roomGamemode by remember { mutableStateOf("") }
    var roomMaxPlayers by remember { mutableIntStateOf(0) }
    var roomDuration by remember { mutableIntStateOf(0) }

    var sessionSocket: SessionWebSocket? by remember { mutableStateOf(null) }
    var socketMessages by remember { mutableStateOf(listOf<String>()) }



    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    fun joinRoom() {
        // TODO: implementa join con roomCode
    }

    fun getGamemodeicon(gamemode: String): Int {
        return when (gamemode) {
            "Bomb Defuse" -> R.drawable.bomb_24px
            "Team Deathmatch" -> R.drawable.skull_24px
            "Free for All" -> R.drawable.swords_24px
            else -> R.drawable.ic_launcher_foreground
        }
    }

    suspend fun leaveRoom() {
        showBottomSheet = false
        apiContext.leaveSession(roomCode)
        sessionSocket?.disconnect()
    }

    suspend fun startRoom(id: String) {
        showBottomSheet = true
        val response = apiContext.createSession(id)
        Log.d("roomcode", "$response")
        response.body()?.let { matchesResponse ->
            roomCode = matchesResponse.session_room_code

            sessionSocket = SessionWebSocket(roomCode) { msg ->
                socketMessages = socketMessages + msg
                Log.d("webSocket" ,"message:$msg" )
            }
            sessionSocket?.connect()
        }

        val response2 = apiContext.getMatch(id)
        response2.body()?.let { matchResponse ->
            roomName = matchResponse.configuration_name
            roomGamemode = matchResponse.game_mode_name
            roomDuration = matchResponse.match_duration_minutes
            roomMaxPlayers = matchResponse.max_players
        }
    }

    // 🔹 GESTIONE DEL PULL TO REFRESH
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            try {
                val userResponse = apiContext.getUser()
                if (userResponse.isSuccessful) {
                    error = null
                } else {
                    error = "Errore refresh: codice ${userResponse.code()}"
                }
            } catch (e: Exception) {
                error = "Errore durante il refresh: ${e.localizedMessage}"
            } finally {
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        val currentAccessToken = tokenManager.accessToken.firstOrNull()
        val currentRefreshToken = tokenManager.refreshToken.firstOrNull()

        Log.d("Home", "Access token: $currentAccessToken")
        Log.d("Home", "Refresh token: $currentRefreshToken")

        if (!currentAccessToken.isNullOrEmpty()) {
            try {
                val userResponse = apiContext.getUser()
                if (userResponse.isSuccessful) {
                    isLoading = false
                    return@LaunchedEffect
                } else if (userResponse.code() == 401 && !currentRefreshToken.isNullOrEmpty()) {
                    val refreshResponse =
                        apiContext.refreshToken(refreshTokenRequest(currentRefreshToken))
                    val newAccess = refreshResponse.body()?.access
                    if (refreshResponse.isSuccessful && !newAccess.isNullOrEmpty()) {
                        tokenManager.saveTokens(newAccess, currentRefreshToken)
                    } else {
                        tokenManager.clearTokens()
                        navController.navigate("login") {
                            popUpTo("home/$username") { inclusive = true }
                        }
                    }
                } else {
                    tokenManager.clearTokens()
                    navController.navigate("login") {
                        popUpTo("home/$username") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                error = "Errore chiamata getUser: ${e.localizedMessage}"
                tokenManager.clearTokens()
                navController.navigate("login") {
                    popUpTo("home/$username") { inclusive = true }
                }
            }
        } else if (!currentRefreshToken.isNullOrEmpty()) {
            try {
                val refreshResponse =
                    apiContext.refreshToken(refreshTokenRequest(currentRefreshToken))
                val newAccess = refreshResponse.body()?.access
                if (refreshResponse.isSuccessful && !newAccess.isNullOrEmpty()) {
                    tokenManager.saveTokens(newAccess, currentRefreshToken)
                } else {
                    tokenManager.clearTokens()
                    navController.navigate("login") {
                        popUpTo("home/$username") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                error = "Errore durante il refresh: ${e.localizedMessage}"
                tokenManager.clearTokens()
                navController.navigate("login") {
                    popUpTo("home/$username") { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("home/$username") { inclusive = true }
            }
        }

        isLoading = false
    }

    // 🔹 UI con supporto al pull-to-refresh
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true }
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Commander",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomInput(
                        value = roomCode2,
                        onValueChange = { roomCode2 = it },
                        label = "Room Code"
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Btn(
                        onClick = { joinRoom() },
                        text = "Join Room",
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = "Your Rooms",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 20.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ScrollableRowsLazy { id ->
                    scope.launch { startRoom(id) }
                }

                if (error != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }


                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { scope.launch { leaveRoom() } },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        sheetState = sheetState
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                                .heightIn(min = 700.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = roomName,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
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
                                        painter = painterResource(id = getGamemodeicon(roomGamemode)),
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

                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Room code:",
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = roomCode,
                                    fontSize = 50.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                Btn(
                                    text = "Create Teams",
                                    shape = AppShapes.medium,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        (context as MainActivity).setActiveSession(roomCode, sessionSocket!!)
                                        navController.navigate("createTeams/$roomCode")
                                    }

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}