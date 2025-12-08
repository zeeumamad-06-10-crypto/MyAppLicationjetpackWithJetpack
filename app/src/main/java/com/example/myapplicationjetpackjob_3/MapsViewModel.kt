package com.example.myapplicationjetpackjob_3


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// User data model (update fields according to your Firestore structure)


class MapsViewModel(private val db: FirebaseFirestore) : ViewModel() {

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    private val _singleUser = MutableStateFlow<User?>(null)
    val singleUser: StateFlow<User?> = _singleUser

    // Load all users from Firestore
    fun loadAllUsers() {
        viewModelScope.launch {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    val list = result.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)?.copy(uid = doc.id)
                    }
                    _allUsers.value = list
                }
                .addOnFailureListener {
                    _allUsers.value = emptyList()
                }
        }
    }

    // Load single user by UID
    fun loadSingleUser(uid: String) {
        viewModelScope.launch {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
                    _singleUser.value = user
                }
                .addOnFailureListener {
                    _singleUser.value = null
                }
        }
    }
}
