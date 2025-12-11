package com.example.myapplicationjetpackjob_3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapsViewModel(private val db: FirebaseFirestore) : ViewModel() {

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    private val _singleUser = MutableStateFlow<User?>(null)
    val singleUser: StateFlow<User?> = _singleUser

    fun loadAllUsers() {
        viewModelScope.launch {
            try {
                val result = db.collection("users").get().await()
                _allUsers.value = result.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(uid = doc.id)
                }
            } catch (e: Exception) {
                _allUsers.value = emptyList()
            }
        }
    }

    fun loadSingleUser(uid: String) {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid).get().await()
                _singleUser.value = doc.toObject(User::class.java)?.copy(uid = doc.id)
            } catch (e: Exception) {
                _singleUser.value = null
            }
        }
    }
}
