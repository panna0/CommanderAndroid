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
import com.example.commander.Screens.CreateTeamsScreen
import com.example.commander.Screens.EditProfileScreen

import com.example.commander.Screens.LoginScreen
import com.example.commander.Screens.MapScreen
import com.example.commander.Screens.ProfileScreen
import com.example.commander.Screens.RegisterScreen
import com.example.commander.Screens.YourMatchesScreen

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

                if (currentRoute != "login" && currentRoute != "register" && currentRoute != "editProfile" && currentRoute !="yourMatches" && currentRoute != "createTeams/{roomCode}/{roomName}/{gamemode}") {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "home/{inGame}",
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
                composable("home/{inGame}") { backStackEntry ->
                    val inGameArg = backStackEntry.arguments?.getString("inGame") ?: "false"
                    val inGame = inGameArg.toBooleanStrictOrNull() ?: false
                    HomeScreen(navController = navController, inGame)
                }
                composable("profile") {
                    ProfileScreen(navController= navController)
                }
                composable("map") {
                    MapScreen(navController= navController)
                }
                composable("editProfile") {
                    EditProfileScreen(navController= navController)
                }

                composable("yourMatches") {
                    YourMatchesScreen(navController= navController)
                }

                composable("createTeams/{roomCode}/{roomName}/{gamemode}") { backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")
                    val roomName = backStackEntry.arguments?.getString("roomName")
                    val gamemode = backStackEntry.arguments?.getString("gamemode")
                    if (roomCode != null) {
                        CreateTeamsScreen(navController = navController, roomCode = roomCode, roomName = roomName, gamemode = gamemode)
                    }
                }
            }
        }
    }
}
