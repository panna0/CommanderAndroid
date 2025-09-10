package com.example.commander.Components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamsCarousel(
    assignments: Map<String, List<String>>,
    onDrop: (String, String) -> Unit
) {
    val items = listOf("Team 1", "Team 2", "Team 3")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { items.size },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { index ->
            val teamId = items[index]
            TeamCard(
                teamId = teamId,
                assignedIcons = assignments[teamId] ?: emptyList(),
                onDrop = onDrop
            )
        }
    }
}