package com.example.commander.Components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamCard(
    teamId: String,
    assignedIcons: List<String>,
    onDrop: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(180.dp, 120.dp)
            .background(Color(0xFFBBDEFB))
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        scope.launch {
                            event.dragAndDropClipData?.let { data ->
                                if (data.itemCount > 0) {
                                    val iconId = data.getItemAt(0).text?.toString()
                                    if (!iconId.isNullOrBlank()) {
                                        onDrop(teamId, iconId)
                                    }
                                }
                            }
                        }
                        return true
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Team: $teamId",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            assignedIcons.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}