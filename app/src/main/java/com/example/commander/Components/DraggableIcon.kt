package com.example.commander.Components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableIcon(
    username: String,
    profilePic: String?,
    onDragStart: (String) -> Unit,
) {
    val initials = username
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString()?.uppercase() }
        .take(2)
        .joinToString("")

    Column(verticalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant)
                .dragAndDropSource(

                    drawDragDecoration = {
                        drawCircle(color = Color.LightGray)

                    }
                ) {
                    detectTapGestures(
                        onPress = {
                            onDragStart(username)
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newPlainText("username", username)
                                )
                            )
                        },

                    )
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (profilePic.isNullOrBlank()) {

                Text(
                    text = initials,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
            } else {

                AsyncImage(
                    model = profilePic,
                    contentDescription = "$username profile picture",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }
        }
        Text(username, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
