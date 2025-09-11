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

@Composable
fun IconGrid(
    icons: List<String>,
    onDrop: (String, String) -> Unit,
    onDragStart: (String) -> Unit,
    onDragEnd: (String?) -> Unit
) {
    val height = 200.dp
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.height(height)
    ) {
        items(items = icons, key = { it }) { iconId ->
            Box(
                modifier = Modifier
                    .animateItem()
                    .padding(6.dp)
            ) {
                DraggableIcon(
                    iconId = iconId,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}
