package com.example.commander.Screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.commander.Components.TeamsCarousel
import com.example.commander.MainActivity
import androidx.compose.ui.platform.LocalContext
import com.example.commander.Components.IconGrid

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@SuppressLint("ContextCastToActivity")
@Composable
fun CreateTeamsScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context.findActivity() as? MainActivity

    if (activity == null) return

    BackHandler {
        activity.leaveActiveSession()
        navController.popBackStack()
    }

    DisposableEffect(Unit) {
        onDispose { activity.leaveActiveSession() }
    }

    var assignments by remember {
        mutableStateOf(
            mutableMapOf(
                "Team 1" to mutableListOf<String>(),
                "Team 2" to mutableListOf<String>(),
                "Team 3" to mutableListOf<String>()
            )
        )
    }

    val allIcons = remember { listOf("A","B","C","D","E","F","G","H","I") }

    var draggingIcon by remember { mutableStateOf<String?>(null) }

    val assignedIcons = assignments.values.flatten()
    val availableIcons = allIcons - assignedIcons.toSet()
    val iconsForGrid = availableIcons.filter { it != draggingIcon }



    Column(Modifier.fillMaxSize()) {
        TeamsCarousel(
            assignments = assignments,
            onDrop = { teamId, iconId ->
                assignments[teamId]?.add(iconId)
                assignments = assignments.toMutableMap()
            }
        )

        IconGrid(
            icons = iconsForGrid,
            onDrop = { teamId, iconId ->

                assignments[teamId]?.add(iconId)
                assignments = assignments.toMutableMap()
            },
            onDragStart = { id -> draggingIcon = id },
            onDragEnd = { _ -> draggingIcon = null }
        )
    }
}