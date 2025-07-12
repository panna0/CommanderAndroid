package com.example.commander.Navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.commander.Models.User
import com.example.commander.Screens.HomeScreen
import com.example.commander.Screens.LoginScreen
import com.example.commander.Screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val users = remember { mutableStateListOf<User>() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        NavHost(
            navController = navController,
            startDestination = "login",
            enterTransition = {

                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {

                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            },
            popEnterTransition = {

                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {

                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            composable("login") {
                LoginScreen(navController = navController, users = users)
            }
            composable("register") {
                RegisterScreen(
                    navController = navController,
                    users = users,
                    onRegister = { user ->
                        users.add(user)
                        navController.navigate("login")
                    }
                )
            }
            composable("home/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                HomeScreen(username = username)
            }
        }
    }
}