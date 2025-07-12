

package com.example.commander.Components




import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

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
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors,

    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}
