package com.example.merco242.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.User
import com.example.merco242.repository.AuthRepository
import com.example.merco242.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    // Estado de autenticación: 0: Idle, 1: Loading, 2: Error, 3: Success
    private val _authState = MutableLiveData(0)
    val authState: LiveData<Int> get() = _authState

    // Mensajes de error para mostrar en la vista
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Tipo de usuario seleccionado
    var selectedUserType: String = "buyer"

    // Inicia sesión con credenciales y tipo de usuario
    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _authState.value = 1 // Loading
            }

            val result = authRepository.login(email, password, selectedUserType)
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _authState.value = 3 // Success
                    _errorMessage.value = null
                } else {
                    _authState.value = 2 // Error
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Error desconocido"
                }
            }
        }
    }

    // Registra un nuevo usuario
    fun registerUser(user: User, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _authState.value = 1 // Loading
            }

            val result = authRepository.register(user, password, userType)
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _authState.value = 3 // Success
                    _errorMessage.value = null
                } else {
                    _authState.value = 2 // Error
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Error desconocido"
                }
            }
        }
    }
}
