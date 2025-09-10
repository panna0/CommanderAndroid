package com.example.commander.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.commander.Components.IconGrid
import com.example.commander.Components.TeamsCarousel

@Composable
fun CreateTeamsScreen() {

    val availableIcons = remember {
        mutableStateMapOf(
            "A" to true, "B" to true, "C" to true,
            "D" to true, "E" to true, "F" to true,
            "G" to true, "H" to true, "I" to true
        )
    }

    val assignedIcons = remember {
        mutableStateMapOf(
            "Team 1" to mutableListOf<String>(),
            "Team 2" to mutableListOf<String>(),
            "Team 3" to mutableListOf<String>()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconGrid(
            icons = availableIcons.filter { it.value }.keys.toList(),
            onDrop = { teamId, iconId ->
                // This logic is now handled in TeamsCarousel, but good to have for future
            }
        )

        TeamsCarousel(
            assignments = assignedIcons,
            onDrop = { teamId, iconId ->
                if (availableIcons[iconId] == true) {
                    assignedIcons[teamId]?.add(iconId)
                    availableIcons[iconId] = false
                }
            }
        )
    }
}