package com.example.merco242.repository

import com.example.merco242.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun register(user: User, password: String, userType: String): Result<Boolean>
    suspend fun login(email: String, password: String, userType: String): Result<Boolean>
}

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun register(user: User, password: String, userType: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Error creating user"))

            val userData = hashMapOf(
                "name" to user.name,
                "lastname" to user.lastname,
                "celphone" to user.celphone,
                "email" to user.email,
                "type" to userType
            )

            val collectionName = if (userType == "buyer") "buyers" else "sellers"
            db.collection(collectionName).document(userId).set(userData).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String, userType: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("User not found"))

            val collectionName = if (userType == "buyer") "buyers" else "sellers"
            val userSnapshot = db.collection(collectionName).document(userId).get().await()

            if (userSnapshot.exists()) {
                Result.success(true)
            } else {
                Result.failure(Exception("User type mismatch"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
