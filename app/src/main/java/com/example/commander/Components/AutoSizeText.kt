import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 32.sp,
    minFontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1
) {
    var scaledFontSize by remember { mutableStateOf(maxFontSize) }

    Text(
        text = text,
        fontSize = scaledFontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier,
        onTextLayout = { result ->
            if (result.didOverflowWidth && scaledFontSize > minFontSize) {
                scaledFontSize = (scaledFontSize.value - 1).sp
            }
        }
    )
}
