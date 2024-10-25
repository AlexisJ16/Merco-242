package com.example.merco242.repository

import com.example.merco242.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun signup(user: User, password: String, userType: String): Result<Boolean>
    suspend fun signin(email: String, password: String, userType: String): Result<Boolean>
}

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun signup(user: User, password: String, userType: String): Result<Boolean> {
        return try {
            // Registrar usuario en Firebase Authentication
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Error al crear el usuario"))

            // Guardar información del usuario en Firestore
            val userData = hashMapOf(
                "name" to user.name,
                "lastname" to user.lastname,
                "celphone" to user.celphone,
                "email" to user.email,
                "type" to userType
            )

            db.collection("users").document(userId).set(userData).await()

            // Registro exitoso
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signin(email: String, password: String, userType: String): Result<Boolean> {
        return try {
            // Iniciar sesión en Firebase Authentication
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("No se encontró el usuario"))

            // Verificar en Firestore el tipo de usuario
            val userSnapshot = db.collection("users").document(userId).get().await()
            if (userSnapshot.exists()) {
                val userTypeInDb = userSnapshot.getString("type")
                if (userTypeInDb == userType) {
                    // El tipo de usuario coincide
                    Result.success(true)
                } else {
                    // Error por tipo de usuario incorrecto
                    Result.failure(Exception("El tipo de usuario no coincide"))
                }
            } else {
                // Error si no se encuentra el usuario en la base de datos
                Result.failure(Exception("Usuario no registrado en la base de datos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
