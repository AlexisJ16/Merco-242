package com.example.merco242.repository

import com.example.merco242.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            // Crear usuario en Firebase Authentication
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = result.user?.uid
                ?: return Result.failure(Exception("Error creando el usuario: UID no encontrado"))

            // Intentar guardar los datos del usuario en Firestore
            try {
                val userData = hashMapOf(
                    "id" to userId,
                    "name" to user.name,
                    "lastname" to user.lastname,
                    "celphone" to user.celphone,
                    "email" to user.email,
                    "type" to userType
                )

                // Determinar la colección según el tipo de usuario
                val collectionName = if (userType == "buyer") "buyers" else "sellers"

                // Verificar si el documento ya existe, y agregarlo si no existe
                db.collection(collectionName).document(userId).get().await().let { document ->
                    if (!document.exists()) {
                        db.collection(collectionName).document(userId)
                            .set(userData, SetOptions.merge()).await()
                    }
                }

                // Registro exitoso
                Result.success(true)
            } catch (e: Exception) {
                // Error al guardar en Firestore, eliminar usuario de Firebase Auth
                auth.currentUser?.delete()
                Result.failure(Exception("Error al guardar en Firestore: ${e.message}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error en Firebase Auth: ${e.message}"))
        }
    }

    override suspend fun login(email: String, password: String, userType: String): Result<Boolean> {
        return try {
            // Intentar autenticación en Firebase
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId =
                result.user?.uid ?: return Result.failure(Exception("Usuario no encontrado"))

            // Determinar la colección correcta según el tipo de usuario
            val collectionName = if (userType == "buyer") "buyers" else "sellers"

            // Verificar la existencia del documento en la colección específica
            val userSnapshot = db.collection(collectionName).document(userId).get().await()

            if (userSnapshot.exists()) {
                Result.success(true)
            } else {
                // Si el usuario no está en la colección esperada, cerramos sesión
                auth.signOut()
                Result.failure(Exception("Error: el tipo de usuario no coincide o no existe en la colección $collectionName"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error en el inicio de sesión: ${e.message}"))
        }
    }
}
