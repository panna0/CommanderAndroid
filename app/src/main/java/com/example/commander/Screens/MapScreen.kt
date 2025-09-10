package com.example.commander.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.commander.R
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap

import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import java.lang.SecurityException

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userLocation = remember { mutableStateOf<Point?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            userLocation.value = Point.fromLngLat(location.longitude, location.latitude)
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            } else {
                // Permessi negati: qui puoi mostrare un messaggio di errore
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation.value = Point.fromLngLat(location.longitude, location.latitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(5.0)
            center(Point.fromLngLat(-98.0, 39.5))
        }

    }


    LaunchedEffect(userLocation.value) {
        userLocation.value?.let { location ->
            Log.d("MapScreen", "Nuova posizione: $location")
            mapViewportState.setCameraOptions {
                zoom(14.0)
                center(location)
            }
        }
    }



    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        mapInitOptionsFactory = { context ->
            MapInitOptions(
                context = context,
                styleUri = "mapbox://styles/mapbox/dark-v11"
            )
        }
    ){

        userLocation.value?.let { point ->
            CircleAnnotation(
                point = point,
                circleRadius = 8.0,
                circleStrokeWidth = 2.0,
                circleColorInt = android.graphics.Color.parseColor("#24a83f"),
                circleStrokeColorInt = android.graphics.Color.parseColor("#00ff33")
            )
        }
    }


}
