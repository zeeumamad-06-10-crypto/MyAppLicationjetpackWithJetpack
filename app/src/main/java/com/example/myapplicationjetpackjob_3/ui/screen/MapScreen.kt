package com.example.myapplicationjetpackjob_3.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationjetpackjob_3.MapsViewModel
import com.example.myapplicationjetpackjob_3.MapsViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.firestore.FirebaseFirestore


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MarkerState


@Composable
fun MapScreen(
    showAll: Boolean = false,
    userId: String? = null
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val viewModel: MapsViewModel = viewModel(factory = MapsViewModelFactory(db))

    val defaultLatLng = LatLng(23.777176, 90.399452)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 6f)
    }

    // Load users
    LaunchedEffect(Unit) {
        if (showAll) viewModel.loadAllUsers()
        else if (!userId.isNullOrEmpty()) viewModel.loadSingleUser(userId)
    }

    val singleUser by viewModel.singleUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    // Auto-zoom
    LaunchedEffect(showAll, allUsers, singleUser) {
        if (showAll && allUsers.isNotEmpty()) {
            val bounds = LatLngBounds.builder().apply {
                allUsers.forEach { user ->
                    if (user.latitude != null && user.longitude != null) {
                        include(LatLng(user.latitude, user.longitude))
                    }
                }
            }.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        } else if (!showAll && singleUser != null) {
            val user = singleUser!!
            if (user.latitude != null && user.longitude != null) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(user.latitude, user.longitude),
                        6f
                    )
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Single User
            singleUser?.let { user ->
                if (user.latitude != null && user.longitude != null) {
                    Marker(
                        state = MarkerState(LatLng(user.latitude, user.longitude)),
                        title = user.name.ifBlank { user.email }
                    )
                }
            }

            // All Users
            allUsers.forEach { user ->
                if (user.latitude != null && user.longitude != null) {
                    Marker(
                        state = MarkerState(LatLng(user.latitude, user.longitude)),
                        title = user.name.ifEmpty { user.email }
                    )
                }
            }
        }
    }
}


