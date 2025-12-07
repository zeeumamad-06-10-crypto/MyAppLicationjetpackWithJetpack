package com.example.myapplicationjetpackjob_3.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplicationjetpackjob_3.AuthRepository
import com.example.myapplicationjetpackjob_3.AuthViewModel
import com.example.myapplicationjetpackjob_3.ui.screen.HomeScreen
import com.example.myapplicationjetpackjob_3.Activities.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        val authViewModel = AuthViewModel(authRepository)

        setContent {
            val context = this
            HomeScreen(
                viewModel = authViewModel,
                onNavigateSignUp = {
                    authViewModel.signOut() // Sign out user
                    // Navigate to SignInActivity
                    startActivity(Intent(context, SignInActivity::class.java))
                    finish() // Close HomeActivity
                }
            )
        }
    }
}
