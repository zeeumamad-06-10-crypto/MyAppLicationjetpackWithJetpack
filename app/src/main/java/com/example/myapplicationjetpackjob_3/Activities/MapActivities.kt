package com.example.myapplicationjetpackjob_3.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplicationjetpackjob_3.AuthRepository
import com.example.myapplicationjetpackjob_3.AuthViewModel
import com.example.myapplicationjetpackjob_3.MapsViewModel
import com.example.myapplicationjetpackjob_3.MapsViewModelFactory
import com.example.myapplicationjetpackjob_3.ui.screen.MapScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class MapActivities : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val mapsViewModel: MapsViewModel = MapsViewModelFactory(db)
            .create(MapsViewModel::class.java)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "map_main") {

                composable("map_main") {
                    MapScreen(
                        mapsViewModel = mapsViewModel,
                        navController = navController,
                        onNavigateSignUp = {
                            // Example: navigate back to sign-in
                        }
                    )
                }

                composable(
                    route = "map_screen?userId={userId}&showAll={showAll}",
                    arguments = listOf(
                        navArgument("userId") { defaultValue = "" },
                        navArgument("showAll") { type = NavType.BoolType; defaultValue = false }
                    )
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    val showAll = backStackEntry.arguments?.getBoolean("showAll") ?: false

                    MapScreen(
                        mapsViewModel = mapsViewModel,
                        navController = navController,
                        userId = if (userId.isNullOrEmpty()) null else userId,
                        showAll = showAll
                    )
                }
            }
        }
    }
}


