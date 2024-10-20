package com.example.icesiapp242.repository

import com.example.icesiapp242.domain.model.User
import com.example.icesiapp242.service.AuthService
import com.example.icesiapp242.service.AuthServiceImpl
import com.google.firebase.auth.auth
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
        //2. Obtenemos el UID
        val uid = Firebase.auth.currentUser?.uid
        //3. Crear el usuario en Firestore
        uid?.let {
            user.id = it
            userRepository.createUser(user)
        }
    }

    override suspend fun signin(email: String, password: String) {
        authService.loginWithEmailAndPassword(email, password)
    }
}