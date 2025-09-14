package com.example.commander.Components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.commander.MainActivity
import com.example.commander.findActivity

data class NavItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context.findActivity() as? MainActivity ?: return
    val route = if (activity.inSession){"match/{${activity.currentRoomCode}}"}else{"home"}
    val items = listOf(
        NavItem("Home", route) {
            Icon(
                Icons.Default.Home,
                contentDescription = "Home"
            )
        },
        NavItem("Map", "map") { Icon(Icons.Default.Map, contentDescription = "Profile") },
        NavItem("Profile", "profile") {
            Icon(
                Icons.Default.Person,
                contentDescription = "Settings"
            )
        }
    )
    Column {


        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        NavigationBar(containerColor = MaterialTheme.colorScheme.background,) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEach { item ->
                NavigationBarItem(
                    icon = { item.icon() },
                    label = { Text(item.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
