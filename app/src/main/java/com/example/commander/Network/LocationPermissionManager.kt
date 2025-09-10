package com.example.commander.Network

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class LocationPermissionManager {
    // Stato che indica se il permesso è stato concesso
    var isPermissionGranted by mutableStateOf(false)
        private set

    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
        private set

    @Composable
    fun setupPermissionLauncher() {
        val context = LocalContext.current

        permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                isPermissionGranted = isGranted
            }
        )

        LaunchedEffect(Unit) {

            isPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


            if (!isPermissionGranted) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}