package com.example.commander.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.commander.Models.Player

@Composable
fun IconGrid(
    icons: List<Player>,
    onDragStart: (String) -> Unit,
) {
    val height = 200.dp
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.height(height)
    ) {
        items(items = icons, key = { it.id ?: it.username }) { player ->
            Box(
                modifier = Modifier
                    .animateItem()
                    .padding(6.dp)
            ) {
                DraggableIcon(
                    username = player.username,
                    profilePic = player.profile_image,
                    onDragStart = { onDragStart(player.username) },
                )
            }
        }
    }
}
