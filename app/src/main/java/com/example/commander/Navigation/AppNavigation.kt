package com.example.commander.Navigation

import MatchScreen
import android.content.Intent
import android.nfc.NfcAdapter
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.commander.Components.BottomNavBar

import com.example.commander.Models.User
import com.example.commander.Screens.ChangePasswordScreen
import com.example.commander.Screens.CreateTeamsScreen
import com.example.commander.Screens.EditProfileScreen
import com.example.commander.Screens.HomeScreen

import com.example.commander.Screens.LoginScreen
import com.example.commander.Screens.MapScreen
import com.example.commander.Screens.MatchResultScreen
import com.example.commander.Screens.ProfileScreen
import com.example.commander.Screens.RegisterScreen
import com.example.commander.Screens.WaitAdminScreen
import com.example.commander.Screens.WaitingPlayerScreen
import com.example.commander.Screens.YourMatchesScreen

@Composable
fun AppNavigation(nfcTagId: State<String?>, nfcIntent: Intent?) {
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
                if (currentRoute != "login" && currentRoute != "register" && currentRoute != "editProfile" && currentRoute != "yourMatches" && currentRoute != "createTeams/{roomCode}/{roomName}/{gamemode}" && currentRoute != "waitingPlayers/{roomCode}/{roomName}/{gamemode}" && currentRoute != "matchResult/{roomCode}/{admin}/{winner}/{myUsername}/{roomGamemode}/{roomName}/{myTeam}" && currentRoute != "wait_admin_screen/{roomCode}") {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
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
                composable("home") {
                    HomeScreen(navController = navController)
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
                composable("match/{roomCode}"){ backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")

                    if (roomCode != null) {
                        MatchScreen(roomCode = roomCode, navController = navController, nfcTagId = nfcTagId)
                    }
                }
                composable("createTeams/{roomCode}/{roomName}/{gamemode}") { backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")
                    val roomName = backStackEntry.arguments?.getString("roomName")
                    val gamemode = backStackEntry.arguments?.getString("gamemode")
                    if (roomCode != null) {
                        CreateTeamsScreen(navController = navController, roomCode = roomCode, roomName = roomName, gamemode = gamemode)
                    }
                }
                composable("waitingPlayers/{roomCode}/{roomName}/{gamemode}") { backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")
                    val roomName = backStackEntry.arguments?.getString("roomName")
                    val gamemode = backStackEntry.arguments?.getString("gamemode")
                    if (roomCode != null) {
                        WaitingPlayerScreen(navController = navController, roomCode = roomCode, roomName = roomName, gamemode = gamemode)
                    }
                }
                composable("wait_admin_screen/{roomCode}"){ backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")

                    if (roomCode != null) {
                        WaitAdminScreen(roomCode = roomCode, navController = navController)
                    }
                }

                composable("matchResult/{roomCode}/{admin}/{winner}/{myUsername}/{roomGamemode}/{roomName}/{myTeam}"){ backStackEntry ->
                    val roomCode = backStackEntry.arguments?.getString("roomCode")
                    val winner = backStackEntry.arguments?.getString("winner")
                    val admin = backStackEntry.arguments?.getString("admin")
                    val myUsername = backStackEntry.arguments?.getString("myUsername")
                    val roomGamemode = backStackEntry.arguments?.getString("roomGamemode")
                    val roomName = backStackEntry.arguments?.getString("roomName")
                    val myTeam = backStackEntry.arguments?.getString("myTeam")

                    if (roomCode != null) {
                        MatchResultScreen(roomCode = roomCode, navController = navController, winner = winner, admin = admin, myUsername = myUsername, roomGamemode = roomGamemode ?: "" , roomName = roomName ?: "", myTeam = myTeam )
                    }
                }

                composable("changePassword") {
                    ChangePasswordScreen(navController= navController)
            }
        }
    }
}}