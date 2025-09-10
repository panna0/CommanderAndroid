package com.example.commander.Components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

// Funzione helper per convertire un URI in un File temporaneo
fun Uri.toFile(context: android.content.Context): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val file = File(context.cacheDir, "temp_profile_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        inputStream?.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun EditableProfilePhoto(
    modifier: Modifier = Modifier,
    initialPhoto: File? = null,
    displayName: String? = null,
    size: Dp = 120.dp,
    shape: Shape = CircleShape,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentDescription: String = "Immagine del profilo",
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onPhotoChanged: (File?) -> Unit = {},
) {
    var photo by remember { mutableStateOf(initialPhoto) }
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            val newFile = uri?.toFile(context)

            photo = newFile
            onPhotoChanged(newFile)
        }
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(containerColor)
                .border(BorderStroke(borderWidth, borderColor), shape)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(size / 2))
                photo != null -> ProfilePhotoImage(
                    photoFile = photo,
                    shape = shape
                )
                !displayName.isNullOrBlank() -> InitialsAvatar(
                    name = displayName,
                    shape = shape
                )
            }
        }

        // FAB sopra, non clippato
        AnimatedVisibility(
            visible = enabled && !isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .wrapContentSize()
        ) {
            SmallFloatingActionButton(
                onClick = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = CircleShape,
                modifier = Modifier.size((size * 0.35f).coerceAtLeast(28.dp)),
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Modifica foto profilo"
                )
            }
        }
    }
}

@Composable
private fun ProfilePhotoImage(
    photoFile: File?,
    shape: Shape,
) {
    val ctx = LocalContext.current
    val request = remember(photoFile) {
        photoFile?.let {
            ImageRequest.Builder(ctx)
                .data(it)
                .crossfade(true)
                .build()
        }
    }

    if (request != null) {
        AsyncImage(
            model = request,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun InitialsAvatar(
    name: String,
    shape: Shape,
) {
    val initials = remember(name) {
        name.trim()
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    }

    val bg = MaterialTheme.colorScheme.secondaryContainer
    val fg = MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            textAlign = TextAlign.Center,
        )
    }
}