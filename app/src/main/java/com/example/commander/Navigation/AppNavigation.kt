package com.example.commander.Navigation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.commander.Screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    val users = remember { mutableStateListOf<Pair<String, String>>() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, users = users)
        }
        composable("register") {
            RegisterScreen(navController = navController, users = users)
        }
        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            HomeScreen(username = username)
        }
    }
    }
}
