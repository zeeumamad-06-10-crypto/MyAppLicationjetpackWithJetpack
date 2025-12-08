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
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onNavigateSignUp: () -> Unit,
    navController: NavController
) {
    val activity = LocalActivity.current
    val currentUser by viewModel.user.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val context = LocalContext.current

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

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

            Text(
                text = "${user.name} (${user.email})",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Navigate using email as ID (or some unique field)
                        navController.navigate("map_screen?userEmail=${user.email}")
                    }
                    .padding(16.dp)
            )


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

                    TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = latitude, onValueChange = {}, label = { Text("Latitude") }, readOnly = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = longitude, onValueChange = {}, label = { Text("Longitude") }, readOnly = true)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    latitude = location.latitude.toString()
                                    longitude = location.longitude.toString()
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }) {
                        Text("Get Current Location")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        val updatedUser = user.copy(name = name, latitude = lat, longitude = lon)
                        viewModel.updateUser(updatedUser)
                        showEdit = false
                    }) {
                        Text("Save")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Button to open all users on map
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("map_screen?showAll=true") }) {
                Text("Open Map")
            }
        }

        // Sign Out
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.signOut()
                activity?.finish()
                onNavigateSignUp()
            }) {
                Text("Sign Out")
            }
        }
    }
}

