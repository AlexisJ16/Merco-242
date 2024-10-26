package com.example.merco242.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.User
import com.example.merco242.repository.AuthRepository
import com.example.merco242.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    val authState = MutableLiveData(0)
    val errorMessage = MutableLiveData<String?>()

    fun registerUser(user: User, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { authState.value = 1 }
            try {
                authRepository.register(user, password, userType)
                withContext(Dispatchers.Main) { authState.value = 3 }
            } catch (ex: FirebaseAuthException) {
                withContext(Dispatchers.Main) {
                    authState.value = 2
                    errorMessage.value = ex.message
                }
            }
        }
    }

    fun loginUser(email: String, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { authState.value = 1 }
                authRepository.login(email, password, userType)
                withContext(Dispatchers.Main) { authState.value = 3 }
            } catch (ex: FirebaseAuthException) {
                withContext(Dispatchers.Main) {
                    authState.value = 2
                    errorMessage.value = ex.message
                }
            }
        }
    }
}
