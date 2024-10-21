package com.example.merco242.repository

import android.util.Log
import com.example.merco242.domain.model.User
import com.example.merco242.service.AuthService
import com.example.merco242.service.AuthServiceImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthRepository {
    suspend fun signup(user:User, password:String)
    suspend fun signin(email:String, password:String)
}

class AuthRepositoryImpl(
    val authService: AuthService = AuthServiceImpl(),
    val userRepository: UserRepository = UserRepositoryImpl()
) : AuthRepository{
    override suspend fun signup(user: User, password: String) {
        //1. Registro en modulo de autenticación
        authService.createUser(user.email, password)
        Log.e("AuthRepositoryImpl", "Usuario registrado con éxito")
        //2. Obtenemos el UID
        val uid = Firebase.auth.currentUser?.uid
        Log.v("AuthRepositoryImpl", "UID: $uid")
        //3. Crear el usuario en Firestore
        uid?.let {
            user.id = it
            Log.e("AuthRepositoryImpl   USEDID", user.id)
            userRepository.createUser(user)
        }
        Log.e("AuthRepositoryImpl", "Usuario registrado con éxito")
    }

    override suspend fun signin(email: String, password: String) {
        authService.loginWithEmailAndPassword(email, password)
    }
}

