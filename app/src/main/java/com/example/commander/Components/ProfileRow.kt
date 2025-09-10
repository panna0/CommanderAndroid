package com.example.commander.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileRow(
    label: String? = null,
    value: String, // Value is now optional
    modifier: Modifier = Modifier,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    leadingIcon: ImageVector? = null // This should be an ImageVector
) {
    Box(Modifier.padding(8.dp)){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(enabled = isClickable) { onClick() }
                .height(85.dp)

                .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),

            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Icon (optional)
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    modifier = Modifier.padding(16.dp, 0.dp),
                    tint = if (value == "Log out"){ MaterialTheme.colorScheme.error}else{MaterialTheme.colorScheme.secondary}

                )
            }

            // Text Content
            Column(
                modifier = if(leadingIcon == null){Modifier.weight(1f).padding(16.dp)}else{Modifier.weight(1f).padding(0.dp)}
            ) {
                if (label != null) {
                    Text(text = label, fontWeight = FontWeight.Light, fontSize = 14.sp)
                }
                Spacer(Modifier.height(4.dp))

                Text(text = value, fontWeight = FontWeight.Normal, fontSize = 18.sp)

            }


            if (isClickable) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Go to $label",
                    modifier = Modifier.padding(16.dp, 0.dp)
                )
            }
        }
    }
    }
