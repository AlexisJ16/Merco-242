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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore

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

    private val db = FirebaseFirestore.getInstance()

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
            try {
                // Seleccionar la colección correcta en función del tipo de usuario
                val collectionName = if (selectedUserType == "buyer") "buyers" else "sellers"

                // Obtener el documento del usuario desde Firestore
                val documentSnapshot = db.collection(collectionName).document(userId).get().await()

                // Convertir el documento en un objeto User y actualizar el LiveData
                val user = documentSnapshot.toObject(User::class.java)
                withContext(Dispatchers.Main) {
                    _userDetails.value = user
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage.value = "Error al cargar los datos del usuario: ${e.message}"
                }
            }
        }
    }
}
