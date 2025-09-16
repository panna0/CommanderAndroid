package com.example.commander.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle


data class POI(
    val point: Point,
    val label: String
)

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userLocation = remember { mutableStateOf<Point?>(null) }
    val shouldRequestPermission = remember { mutableStateOf(false) }

    val pois = listOf(
        POI(Point.fromLngLat(9.2425517, 45.6787022 ), "POI A"),
        POI(Point.fromLngLat(-98.6, 39.6), "POI B"),
        POI(Point.fromLngLat(-98.7, 39.7), "POI C")
    )

    // POI selezionato (per la label)
    var selectedPoi by remember { mutableStateOf<POI?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation.value = Point.fromLngLat(location.longitude, location.latitude)
                    }
                }
            } else {
                shouldRequestPermission.value = true
            }
        }
    )

    // Controllo permessi
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation.value = Point.fromLngLat(location.longitude, location.latitude)
                }
            }
        } else {
            shouldRequestPermission.value = true
        }
    }

    // Se devo richiedere permesso → lo lancio
    LaunchedEffect(shouldRequestPermission.value) {
        if (shouldRequestPermission.value) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            shouldRequestPermission.value = false
        }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(5.0)
            center(Point.fromLngLat(-98.0, 39.5))
        }
    }

    // Quando cambia la posizione utente → muovo la camera
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
        style = { MapStyle(style = Style.DARK) },

    ) {

        userLocation.value?.let { point ->
            CircleAnnotation(
                point = point,

            ){
                circleRadius = 8.0
                circleStrokeWidth = 2.0
                circleColor = Color(0xFF24A83F)
                circleStrokeColor = Color(0xFF00FF33)
            }
        }


        val marker = rememberIconImage(key = "markerResourceId", painter = rememberVectorPainter(
            Icons.Filled.LocationOn))
            pois.forEach { poi ->
            PointAnnotation(
                point = poi.point,

                onClick = {

                    selectedPoi = if (selectedPoi == poi) null else poi
                    true
                }
            ){
                iconImage = marker
                iconSize = 1.5
            }
        }

        selectedPoi?.let { poi ->
            PointAnnotation(
                point = poi.point
            ) {
                textField = poi.label
                textSize = 14.0
                textOffset = listOf(0.0, -2.0)
            }
        }
    }
}