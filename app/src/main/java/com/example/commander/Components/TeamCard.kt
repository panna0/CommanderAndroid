package com.example.commander.Components

import android.annotation.SuppressLint
import android.content.ClipDescription
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commander.UI.AppShapes
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamCard(
    teamId: String?,
    assignedIcons: List<String>,
    onDrop: (String, String) -> Unit,
    color: Color,
    teamName: String?,
    onShowBottomSheet: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(color, RoundedCornerShape(16.dp))
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
                                onDrop(teamId.toString(), icon)
                            return true
                        }
                    }
                }

            ),
        contentAlignment = Alignment.Center
    ) {

        Column(modifier = Modifier.fillMaxHeight().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = teamName.toString(),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = assignedIcons.size.toString(),
                fontSize = 100.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = { onShowBottomSheet()},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
            ) {
                Text(text = "View", Modifier.padding(8.dp))
            }
        }
    }
}