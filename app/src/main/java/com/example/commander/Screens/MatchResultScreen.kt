package com.example.commander.Screens

import AutoSizeText
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.commander.Components.Btn
import com.example.commander.MainActivity
import com.example.commander.Models.EndSessionResponse
import com.example.commander.Network.ApiContext
import com.example.commander.findActivity
import kotlinx.coroutines.launch


@Composable
fun MatchResultScreen(
    navController: NavController,
    admin: String?,
    roomCode: String,
    winner: String?,
    myUsername: String?,
    roomGamemode: String,
    roomName: String,
    myTeam: String?
) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }
    val activity = context.findActivity() as? MainActivity ?: return

    val scope = rememberCoroutineScope()

    var cardTitle by remember { mutableStateOf("") }
    var cardDescription by remember { mutableStateOf("") }
    var cardColor by remember { mutableStateOf(Color.Gray) }

    fun getMatchResult(): String {
        if (winner != null) {
            return when {
                winner == "Blue Red" -> "tie"
                "$myTeam" == winner -> "victory"
                myUsername?.let { winner.contains(it) } == true -> "victory"
                else -> "loss"
            }
        }
        return "loss"
    }

    LaunchedEffect(myTeam) {
        getMatchResult()
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (getMatchResult()) {
            "victory" -> {
                cardTitle = "Mission Accomplished"
                cardDescription = "You outsmarted the enemy and completed the mission with precision. Victory is yours."
                cardColor = Color(0xFF4CAF50)
            }
            "loss" -> {
                cardTitle = "Mission Failed"
                cardDescription = "The enemy gained the upper hand this time. Regroup, adapt, and try again."
                cardColor = Color(0xFFF44336)
            }
            "tie" -> {
                cardTitle = "Stalemate"
                cardDescription = "Neither side could secure the objective. The battle ends in a deadlock."
                cardColor = Color(0xFFFFEB3B)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AutoSizeText(
                text = roomName.replace("+", " "),
                maxFontSize = 32.sp,
                minFontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                maxLines = 1
            )

            Text(
                text = "Match Result",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = cardTitle,
                        color = cardColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cardDescription,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(40.dp))
                    Btn(
                        text = "Continue",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            activity.endGame()
                            if (admin == myUsername) {
                                if (roomGamemode == "Free+for+All") {
                                    navController.navigate("waitingPlayers/$roomCode/$roomName/$roomGamemode")
                                } else {

                                    navController.navigate("createTeams/$roomCode/$roomName/${
                                        roomGamemode.replace(
                                            "+",
                                            " "
                                        )
                                    }")
                                }
                            } else {
                                navController.navigate("wait_admin_screen/$roomCode")
                            }
                        }
                    )
                }
            }
        }
    }
}
