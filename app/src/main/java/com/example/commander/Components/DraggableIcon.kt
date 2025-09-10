package com.example.commander.Components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableIcon(iconId: String) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color.LightGray)
            .dragAndDropSource(
                transferData = { _: Offset ->
                    DragAndDropTransferData(
                        clipData = ClipData.newPlainText("iconId", iconId)
                    )
                }
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(iconId, style = MaterialTheme.typography.bodyMedium)
    }
}