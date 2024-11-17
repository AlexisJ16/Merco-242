package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.User
import com.example.merco242.repository.AuthRepository
import com.example.merco242.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var authState: Int = 0 // 0: Idle, 1: Loading, 2: Error, 3: Success

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authState = 1
            val result = authRepository.login(email, password, "buyer") // Default userType
            authState = if (result.isSuccess) 3 else 2
        }
    }

    fun registerUser(user: User, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authState = 1
            val result = authRepository.register(user, password, userType)
            authState = if (result.isSuccess) 3 else 2
        }
    }
}
