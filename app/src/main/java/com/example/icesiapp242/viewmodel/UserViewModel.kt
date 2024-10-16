package com.example.icesiapp242.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icesiapp242.domain.model.User
import com.example.icesiapp242.repository.UserRepository
import com.example.icesiapp242.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// UserViewModel para manejar las operaciones relacionadas con el usuario
class UserViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    val userData = MutableLiveData<User?>()
    val updateState = MutableLiveData<Int>()
    // 0: Idle, 1: Loading, 2: Success, 3: Error

    // Cargar datos del usuario desde el repositorio
    fun getUserData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { updateState.value = 1 } // Loading
                val user = userRepository.getUserById(userId)
                withContext(Dispatchers.Main) {
                    userData.value = user
                    updateState.value = 2 // Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateState.value = 3 // Error
                }
                e.printStackTrace()
            }
        }
    }

    // Actualizar datos del usuario
    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { updateState.value = 1 } // Loading
                userRepository.updateUser(user)
                withContext(Dispatchers.Main) { updateState.value = 2 } // Success
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { updateState.value = 3 } // Error
                e.printStackTrace()
            }
        }
    }
}
