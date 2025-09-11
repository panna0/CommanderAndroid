package com.example.commander.Components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableIcon(
    iconId: String,
    onDragStart: (String) -> Unit,
    onDragEnd: (String?) -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(Color.LightGray, shape = CircleShape)
            .dragAndDropSource(
                drawDragDecoration = {
                    // drag shadow
                    drawCircle(color = Color.LightGray)
                }
            ) {
                detectTapGestures(
                    onLongPress = {
                        onDragStart(iconId)
                        startTransfer(
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText("iconId", iconId)
                            )
                        )
                        onDragEnd(null)
                    }
                )
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(iconId, style = MaterialTheme.typography.bodyMedium)
    }
}
