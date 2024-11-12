package com.example.merco242.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.User
import com.example.merco242.repository.AuthRepository
import com.example.merco242.repository.AuthRepositoryImpl
import com.example.merco242.repository.UserRepository
import com.example.merco242.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    val authState = MutableLiveData(0) // 0: Idle, 1: Loading, 2: Error, 3: Success
    val errorMessage = MutableLiveData<String?>()
    var selectedUserType: String? = null // Tipo de usuario seleccionado ("buyer" o "seller")

    // Datos de usuario cargados
    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails

    fun setUserType(userType: String) {
        selectedUserType = userType
    }

    fun registerUser(user: User, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { authState.value = 1 } // Loading state
            val result = authRepository.register(user, password, userType)
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    authState.value = 3 // Registro exitoso
                    errorMessage.value = null
                } else {
                    authState.value = 2 // Error en el registro
                    errorMessage.value = result.exceptionOrNull()?.message // Mensaje detallado de error
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        val userType = selectedUserType ?: return // No hacer nada si no hay tipo seleccionado

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { authState.value = 1 } // Loading state
            val result = authRepository.login(email, password, userType)
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    authState.value = 3 // Inicio de sesión exitoso
                    errorMessage.value = null
                } else {
                    authState.value = 2 // Error en el inicio de sesión
                    errorMessage.value = result.exceptionOrNull()?.message // Mensaje detallado de error
                }
            }
        }
    }

    fun loadUserDetails(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getCurrentUser()
            withContext(Dispatchers.Main) {
                _userDetails.value = user
            }
        }
    }
}
