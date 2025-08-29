package com.example.commander.Navigation

import HomeScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.commander.Components.BottomNavBar

import com.example.commander.Models.User

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
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                // Mostra la navbar solo se NON sei in login/register
                if (currentRoute != "login" && currentRoute != "register") {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "home/{username}",
                modifier = Modifier.padding(innerPadding),
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
                    HomeScreen(username = username, navController = navController)
                }
                composable("profile") {
                    Text("Profilo") // TODO: metti la tua ProfileScreen
                }
                composable("settings") {
                    Text("Impostazioni") // TODO: metti la tua SettingsScreen
                }
            }
        }
    }
}
