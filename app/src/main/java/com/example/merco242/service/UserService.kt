package com.example.merco242.service

import android.util.Log
import com.example.merco242.domain.model.User
import com.example.merco242.utils.FirebaseInitializer
import kotlinx.coroutines.tasks.await

interface UserServices {
    suspend fun createUser(user: User)
    suspend fun getUserById(id: String): User?
}

class UserServicesImpl : UserServices {

    private val db = FirebaseInitializer.firestore

    override suspend fun createUser(user: User) {
        try {
            val document = db.collection("users").document(user.id).get().await()
            if (!document.exists()) {
                db.collection("users").document(user.id).set(user).await()
                Log.d("UserServicesImpl", "Usuario creado exitosamente")
            }
        } catch (e: Exception) {
            Log.e("UserServicesImpl", "Error creando usuario: ${e.message}")
            throw e
        }
    }

    override suspend fun getUserById(id: String): User? {
        return try {
            val document = db.collection("users").document(id).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserServicesImpl", "Error obteniendo usuario: ${e.message}")
            null
        }
    }
}
