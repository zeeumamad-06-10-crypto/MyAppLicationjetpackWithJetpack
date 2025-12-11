package com.example.myapplicationjetpackjob_3.Activities

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
import com.example.myapplicationjetpackjob_3.ui.screen.HomeScreen
import com.example.myapplicationjetpackjob_3.ui.screen.MapScreen
import com.example.myapplicationjetpackjob_3.ui.screen.SignInScreen
import com.example.myapplicationjetpackjob_3.ui.screen.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        val authViewModel = AuthViewModel(authRepository)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {

                composable("home") {
                    HomeScreen(
                        viewModel = authViewModel,
                        navController = navController, // this is correct
                        onNavigateSignUp = {
                            authViewModel.signOut()
                            navController.navigate("signIn") { popUpTo("home") { inclusive = true } }

                        }
                    )
                }

                composable("signIn") {
                    SignInScreen(
                        viewModel = authViewModel,
                        onNavigateHome = { navController.navigate("home") },
                        onNavigateSignUp = { navController.navigate("signUp") }
                    )
                }

                composable("signUp") {
                    SignUpScreen(
                        viewModel = authViewModel,
                        onNavigateHome = {
                            navController.navigate("home") {
                                popUpTo("signIn") { inclusive = true }
                            }
                        },
                        onNavigateSignIn = {
                            navController.navigate("signIn") {
                                popUpTo("signUp") { inclusive = true }
                            }
                        }
                    )
                }


            }
        }
    }
}