package com.example.myapplicationjetpackjob_3.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplicationjetpackjob_3.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
@Composable
fun MapScreen(
    mapsViewModel: MapsViewModel,
    navController: NavController,
    userId: String? = null,
    showAll: Boolean = false,
    onNavigateSignUp: (() -> Unit)? = null
) {
    val singleUser by mapsViewModel.singleUser.collectAsState()
    val allUsers by mapsViewModel.allUsers.collectAsState()

    val defaultLatLng = LatLng(23.777176, 90.399452)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 6f)
    }

    LaunchedEffect(Unit) {
        if (showAll) mapsViewModel.loadAllUsers()
        else if (!userId.isNullOrEmpty()) mapsViewModel.loadSingleUser(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Show single user
            singleUser?.let {
                if (it.latitude != null && it.longitude != null) {
                    Marker(
                        state = MarkerState(LatLng(it.latitude, it.longitude)),
                        title = it.name.ifBlank { it.email }
                    )
                }
            }

            // Show all users
            allUsers.forEach {
                if (it.latitude != null && it.longitude != null) {
                    Marker(
                        state = MarkerState(LatLng(it.latitude, it.longitude)),
                        title = it.name.ifBlank { it.email }
                    )
                }
            }
        }
    }
}

