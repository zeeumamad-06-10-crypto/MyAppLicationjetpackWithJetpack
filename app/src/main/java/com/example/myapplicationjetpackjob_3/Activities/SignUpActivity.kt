package com.example.myapplicationjetpackjob_3.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplicationjetpackjob_3.AuthRepository
import com.example.myapplicationjetpackjob_3.AuthViewModel
import com.example.myapplicationjetpackjob_3.ui.screen.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        val authViewModel = AuthViewModel(authRepository)

        setContent {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateHome = {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // close SignUpActivity
                }
            )
        }
    }
}