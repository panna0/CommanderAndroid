package com.example.commander.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commander.Models.Match
import com.example.commander.R
import com.example.commander.UI.AppShapes

@Composable
fun MyRow(match: Match, onShowBottomSheet: () -> Unit) {



    fun getRowImage(match: Match): Int {
        return when (match.game_mode_name) {
            "Bomb Defuse" -> R.drawable.bomb_icon
            "Team Deathmatch" -> R.drawable.death_match_icon
            "Free for All" -> R.drawable.free_for_all_icon
            else -> R.drawable.ic_launcher_foreground
        }
    }






    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Immagine circolare
        Image(
            painter = painterResource(id = getRowImage(match)),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = match.configuration_name,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = match.configuration_description,
                fontWeight = FontWeight.Thin,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box (modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ){
            MyIconButton(
                icon = Icons.Default.PlayArrow,
                onClick =  { onShowBottomSheet() },
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.background
            )
        }




    }
}

