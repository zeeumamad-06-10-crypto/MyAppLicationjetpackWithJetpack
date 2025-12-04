package com.example.myapplicationjetpackjob_3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository): ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _user.value = repository.signUp(email, password)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("AUTH", "Attempting sign in with $email / $password")
                _user.value = repository.signIn(email.trim(), password.trim())
                Log.d("AUTH", "Sign in successful: ${_user.value}")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AUTH", "Sign in failed", e)
            }
        }
    }



    fun signOut() {
        repository.signOut()
        _user.value = null
    }
}
