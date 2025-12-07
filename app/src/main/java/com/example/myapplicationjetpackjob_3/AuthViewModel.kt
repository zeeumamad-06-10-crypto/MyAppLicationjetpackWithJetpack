package com.example.myapplicationjetpackjob_3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // CURRENT USER
    private val _user = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // USERS LIST
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    // -------------------------
    // AUTH FUNCTIONS
    // -------------------------

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.signUp(email, password)
                _user.value = result
                saveUserToDatabase(result)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _user.value = repository.signIn(email.trim(), password.trim())
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
                _user.value = null
                _allUsers.value = emptyList()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // -------------------------
    // FIRESTORE FUNCTIONS
    // -------------------------

    private fun saveUserToDatabase(firebaseUser: FirebaseUser?) {
        firebaseUser ?: return

        val userData = User(
            userId = firebaseUser.uid,
            email = firebaseUser.email ?: "Unknown",
            name = firebaseUser.displayName ?: "User",
            latitude = null,
            longitude = null
        )

        db.collection("users")
            .document(firebaseUser.uid)
            .set(userData)
    }

    fun getAllUsers() {
        repository.getAllUsers { users ->
            _allUsers.value = users
        }
    }

    fun updateUser(user: User) {
        repository.updateUser(user) { success ->
            if (success) getAllUsers()
        }
    }
}
