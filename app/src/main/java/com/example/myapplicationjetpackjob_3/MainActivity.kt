package com.example.myapplicationjetpackjob_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationjetpackjob_3.ui.theme.MyApplicationJetpackJob_3Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        val authViewModel = AuthViewModel(authRepository)

        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "signIn") {
                composable("signIn") {
                    SignInScreen(
                        viewModel = authViewModel,
                        onNavigateHome = {
                            navController.navigate("home") {
                                popUpTo("signIn") { inclusive = true }
                            }
                        },
                        onNavigateSignUp = {
                            navController.navigate("signUp")
                        }
                    )
                }

                composable("signUp") {
                    SignUpScreen(authViewModel) {
                        navController.navigate("home") {
                            popUpTo("signUp") { inclusive = true }
                        }
                    }
                }

                composable("home") { HomeScreen(authViewModel) }
            }


        }
    }
}
@Composable
fun HomeScreen(viewModel: AuthViewModel) {
    // Collect the user state from ViewModel
    val user by viewModel.user.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Welcome ${user?.email ?: "Guest"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.signOut() }) {
            Text("Sign Out")
        }
    }
}
