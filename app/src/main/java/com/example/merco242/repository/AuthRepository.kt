package com.example.merco242.repository

import android.util.Log
import com.example.merco242.domain.model.User
import com.example.merco242.service.AuthService
import com.example.merco242.service.AuthServiceImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun signin(email: String, password: String, userType: String): Result<Boolean>
    suspend fun signup(user: User, password: String, userType: String): Result<Boolean>
}

class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun signin(email: String, password: String, userType: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("No se encontró el usuario"))

            // Verificamos en la base de datos si el usuario existe y su tipo
            val userSnapshot = db.collection("users").document(userId).get().await()

            if (userSnapshot.exists()) {
                val userTypeInDb = userSnapshot.getString("type")
                if (userTypeInDb == userType) {
                    // El tipo de usuario coincide, se autoriza el acceso
                    Result.success(true)
                } else {
                    // El tipo de usuario no coincide
                    Result.failure(Exception("El tipo de usuario no coincide"))
                }
            } else {
                Result.failure(Exception("Usuario no registrado en la base de datos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(user: User, password: String, userType: String): Result<Boolean> {
        return try {
            // Registro del usuario en Firebase Authentication
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Error al crear el usuario"))

            // Guardar los datos adicionales del usuario en Firestore
            val userData = hashMapOf(
                "name" to user.name,
                "lastname" to user.lastname,
                "celphone" to user.celphone,
                "email" to user.email,
                "type" to userType
            )
            db.collection("users").document(userId).set(userData).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
