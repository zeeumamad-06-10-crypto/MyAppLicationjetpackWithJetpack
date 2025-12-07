package com.example.myapplicationjetpackjob_3.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplicationjetpackjob_3.AuthViewModel
import androidx.activity.compose.LocalActivity
import com.google.android.gms.location.LocationServices

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onNavigateSignUp: () -> Unit
) {
    val activity = LocalActivity.current
    val currentUser by viewModel.user.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val context = LocalContext.current

    // Fused Location Client
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Optional: Show message to user
        }
    }

    // Request permission on start
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch users when the screen loads
    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {

        // Header
        item {
            Text(text = "Welcome ${currentUser?.email ?: "Guest"}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "All Users:")
            Spacer(modifier = Modifier.height(8.dp))
        }

        // User list
        items(allUsers) { user ->
            var showEdit by remember { mutableStateOf(false) }
            var name by remember { mutableStateOf(user.name) }
            var latitude by remember { mutableStateOf(user.latitude?.toString() ?: "") }
            var longitude by remember { mutableStateOf(user.longitude?.toString() ?: "") }

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(text = "• (${user.email}) \n ${user.name}")

                Button(
                    onClick = { showEdit = !showEdit },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(if (showEdit) "Cancel" else "Edit")
                }

                if (showEdit) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Name TextField
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") }
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Latitude and Longitude TextFields (read-only)
                    TextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Get Current Location Button
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    if (location != null) {
                                        latitude = location.latitude.toString()
                                        longitude = location.longitude.toString()
                                    }
                                }
                            } else {
                                // Request permission again
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    ) {
                        Text("Get Current Location")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val lat = latitude.toDoubleOrNull()
                            val lon = longitude.toDoubleOrNull()
                            val updatedUser = user.copy(
                                name = name,
                                latitude = lat,
                                longitude = lon
                            )
                            viewModel.updateUser(updatedUser)
                            showEdit = false
                        }
                    ) {
                        Text("Save")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Sign Out Button
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.signOut()   // Sign out user
                    activity?.finish()
                    onNavigateSignUp()    // Navigate to SignIn or SignUp screen
                }
            ) {
                Text("Sign Out")
            }
        }
    }
}
