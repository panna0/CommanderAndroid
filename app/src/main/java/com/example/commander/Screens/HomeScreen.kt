import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Components.ScrollableRowsLazy
import com.example.commander.Storage.TokenManager
import com.example.commander.Network.AuthContext
import com.example.commander.Models.refreshTokenRequest
import com.example.commander.UI.AppShapes
import kotlinx.coroutines.flow.firstOrNull

import kotlinx.coroutines.launch

@Composable
fun HomeScreen(username: String, navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var roomCode by remember { mutableStateOf("") }



    fun joinRoom(){
        return
    }


    LaunchedEffect(Unit) {
        scope.launch {
            // Leggi i token in modo esplicito e attendi i loro valori
            val currentAccessToken = tokenManager.accessToken.firstOrNull()
            val currentRefreshToken = tokenManager.refreshToken.firstOrNull()

            if (currentAccessToken.isNullOrEmpty()) {
                if (!currentRefreshToken.isNullOrEmpty()) {
                    // Tentativo di refresh del token
                    try {
                        val response = AuthContext.refreshToken(refreshTokenRequest(currentRefreshToken))
                        if (response.isSuccessful) {
                            val newAccess = response.body()?.access
                            if (newAccess != null) {
                                tokenManager.saveTokens(newAccess, currentRefreshToken)
                            } else {
                                // Refresh fallito, naviga al login
                                tokenManager.clearTokens() // Pulisci i token non validi
                                navController.navigate("login") {
                                    popUpTo("home/$username") { inclusive = true }
                                }
                            }
                        } else {
                            // Errore HTTP nel refresh, naviga al login
                            tokenManager.clearTokens()
                            navController.navigate("login") {
                                popUpTo("home/$username") { inclusive = true }
                            }
                        }
                    } catch (e: Exception) {
                        error = "Errore refresh token: ${e.localizedMessage}"
                        tokenManager.clearTokens()
                        navController.navigate("login") {
                            popUpTo("home/$username") { inclusive = true }
                        }
                    }
                } else {
                    // Nessun token salvato → vai al login
                    navController.navigate("login") {
                        popUpTo("home/$username") { inclusive = true }
                    }
                }
            }
            isLoading = false
        }
    }

    // UI
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(text = "Commander",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                Column (modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    CustomInput(
                        value = roomCode,
                        onValueChange = {roomCode = it},
                        label = "Room Code"
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Btn(
                        onClick = {},
                        text = "Join Room",

                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
                Text(text = "Your Rooms",
                    modifier = Modifier.align(Alignment.Start).padding(horizontal = 20.dp, vertical = 0.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold)
                ScrollableRowsLazy()
                if (error != null) {

                    Spacer(Modifier.height(16.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
        }

}