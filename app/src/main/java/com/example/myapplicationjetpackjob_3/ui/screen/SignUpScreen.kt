package com.example.myapplicationjetpackjob_3.ui.screen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplicationjetpackjob_3.AuthViewModel

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateHome: () -> Unit,
    onNavigateSignIn:() -> Unit,

) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsState()

    // Navigate to Home automatically after successful signup
    LaunchedEffect(user) {
        if (user != null) {
            onNavigateHome()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.signUp(email, password) }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateSignIn() }) {
            Text("Sign In")
        }


    }
}
