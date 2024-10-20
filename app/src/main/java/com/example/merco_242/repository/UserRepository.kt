package com.example.merco_242.repository

import com.example.merco_242.model.User
import com.example.merco_242.service.UserServices
import com.example.merco_242.service.UserServicesImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun getCurrentUser(): User?
}

class UserRepositoryImpl(
    val userServices: UserServices = UserServicesImpl()
): UserRepository {
    override suspend fun createUser(user: User) {
        userServices.createUser(user)
    }

    override suspend fun getCurrentUser(): User? {
        Firebase.auth.currentUser?.let {
            return userServices.getUserById(it.uid)
        } ?: run {
           return null
        }
    }

}