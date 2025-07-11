package com.example.commander.Navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.commander.Screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    val users = remember { mutableStateListOf<Pair<String, String>>() }

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
