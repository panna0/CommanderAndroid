package com.example.commander.Components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.commander.Models.AddTeamRequest
import com.example.commander.Models.AddTeamResponse
import com.example.commander.Network.ApiContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamsCarousel(
    roomCode: String,
    teams: List<AddTeamResponse?>,
    assignments: Map<String, List<String>>,
    onShowBottomSheet: (Int) -> Unit,
    onDrop: (String, String) -> Unit,
    onTeamsLoaded: (List<AddTeamResponse?>) -> Unit,

    ) {
    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }
    var teams by remember { mutableStateOf<List<AddTeamResponse?>>(emptyList()) }

    LaunchedEffect(Unit) {
        val teamsEs = listOf("Team 1", "Team 2")
        val newTeams = mutableListOf<AddTeamResponse?>()
        for (x in teamsEs) {
            try {
                val response = apiContext.addTeam(roomCode, AddTeamRequest(x))
                if (response.isSuccessful) {
                    response.body()?.let { newTeams.add(it) }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("TeamsCarousel", it) }
            }
        }
        teams = newTeams
        onTeamsLoaded(newTeams)
    }

    val colors = listOf(Color(0xFF4260F5), Color(0xFFF54251))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { teams.size },
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 16.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 16.dp,


        ) { index ->
            val teamId = teams[index]?.id
            val teamName = teams[index]?.team_name
            TeamCard(
                teamId = teamId,
                teamName = teamName,
                assignedIcons = assignments[teamId] ?: emptyList(),
                onDrop = onDrop,
                color = colors[index],
                onShowBottomSheet = { onShowBottomSheet(index) }
            )
        }

    }
}