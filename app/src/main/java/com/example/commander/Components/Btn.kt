

package com.example.commander.Components




import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.commander.UI.AppShapes

@Composable
fun Btn(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = AppShapes.medium,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    small: Boolean = false // <--- nuovo parametro
) {
    val horizontalPadding = if (small) 8.dp else 16.dp
    val verticalPadding = if (small) 4.dp else 8.dp
    val fontSize = if (small) 12.sp else 16.sp

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Text(
            text,
            fontSize = fontSize,
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
        )
    }
}
