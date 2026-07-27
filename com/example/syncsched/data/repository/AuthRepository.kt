package com.example.syncsched.data.repository


import com.example.syncsched.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun registerUser(user: User, password: String): Result<Boolean> {
        return try {
            val existingEmail = db.collection("users")
                .whereEqualTo("email", user.email)
                .get().await()

            val existingUsername = db.collection("users")
                .whereEqualTo("username", user.username)
                .get().await()

            if (!existingEmail.isEmpty || !existingUsername.isEmpty) {
                return Result.failure(Exception("An account with this email or username already exists."))
            }

            val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed")

            val newUser = user.copy(uid = uid)
            db.collection("users").document(uid).set(newUser).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

