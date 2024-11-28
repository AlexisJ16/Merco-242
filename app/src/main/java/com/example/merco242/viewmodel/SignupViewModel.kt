package com.example.merco242.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.User
import com.example.merco242.repository.AuthRepository
import com.example.merco242.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _authState = MutableLiveData(0)
    val authState: LiveData<Int> get() = _authState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var selectedUserType: String = "buyer"

    // Método para verificar si el usuario está autenticado
    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Método para obtener el tipo de usuario desde Firestore
    suspend fun getUserType(): String {
        return try {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userDoc = firestore.collection("users").document(it.uid).get().await()
                userDoc.getString("type") ?: "buyer"
            } ?: "buyer"
        } catch (e: Exception) {
            "buyer" // Valor predeterminado en caso de error
        }
    }

    // Método para cerrar sesión
    fun logout() {
        firebaseAuth.signOut()
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.postValue(1) // Loading
            val result = authRepository.login(email, password, selectedUserType)
            _authState.postValue(if (result.isSuccess) 3 else 2)
            _errorMessage.postValue(result.exceptionOrNull()?.message)
        }
    }

    fun registerUser(user: User, password: String, userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.postValue(1) // Loading
            val result = authRepository.register(user, password, userType)
            _authState.postValue(if (result.isSuccess) 3 else 2)
            _errorMessage.postValue(result.exceptionOrNull()?.message)
        }
    }

    fun fetchUserType(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUser = firebaseAuth.currentUser
                currentUser?.let {
                    val userDoc = firestore.collection("users").document(it.uid).get().await()
                    val userType = userDoc.getString("type") ?: "buyer"
                    onComplete(userType)
                } ?: onComplete("buyer")
            } catch (e: Exception) {
                onComplete("buyer")
            }
        }
    }
}
