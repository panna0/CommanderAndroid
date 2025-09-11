package com.example.commander.Components

import android.annotation.SuppressLint
import android.content.ClipDescription
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
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
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = remember {
                    object : DragAndDropTarget {
                        @SuppressLint("SuspiciousIndentation")
                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val icon = event.toAndroidDragEvent().clipData
                                ?.getItemAt(0)?.text.toString()
                                onDrop(teamId, icon)
                            return true
                        }
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