package com.example.myapplicationjetpackjob_3

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    private val firestore = FirebaseFirestore.getInstance()

    // -----------------------------
    // AUTH FUNCTIONS
    // -----------------------------
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun signUp(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser


    // -----------------------------
    // FIRESTORE FUNCTIONS
    // -----------------------------
    fun getAllUsers(onComplete: (List<User>) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(User::class.java)
                onComplete(list)
            }
    }

    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
