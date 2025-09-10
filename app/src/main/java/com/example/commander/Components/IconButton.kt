package com.example.commander.Components

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun MyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    tint: Color = Color.Unspecified,

) {
    Box(


    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}