package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.Product
import com.example.merco242.domain.model.Reservation
import com.example.merco242.domain.model.Store
import com.example.merco242.repository.ReservationRepository
import com.example.merco242.repository.ReservationRepositoryImpl
import com.example.merco242.repository.StoreRepository
import com.example.merco242.repository.StoreRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuyerViewModel(
    private val storeRepository: StoreRepository = StoreRepositoryImpl(),
    private val reservationRepository: ReservationRepository = ReservationRepositoryImpl()
) : ViewModel() {

    // Lista de tiendas
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> get() = _stores

    // Lista de productos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    // Lista de reservas del usuario
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> get() = _reservations

    // Estado de la ubicación del usuario
    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> get() = _userLocation

    /**
     * Obtiene la lista de tiendas desde el repositorio.
     */
    fun fetchStores() {
        viewModelScope.launch {
            try {
                val result = storeRepository.getStores()
                _stores.value = result
            } catch (e: Exception) {
                // Manejo de errores (puede incluir logs o mensajes a la UI)
                _stores.value = emptyList()
            }
        }
    }

    /**
     * Obtiene la lista de productos desde el repositorio.
     */
    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val result = storeRepository.getStores()
                _products.value = result.flatMap { it.products } // Procesar productos desde las tiendas
            } catch (e: Exception) {
                _products.value = emptyList()
            }
        }
    }


    /**
     * Obtiene una tienda específica por su ID.
     */
    fun getStoreById(id: String): StateFlow<Store?> {
        val storeFlow = MutableStateFlow<Store?>(null)
        viewModelScope.launch {
            storeFlow.value = _stores.value.find { it.id == id }
        }
        return storeFlow
    }


    fun fetchReservations() {
        viewModelScope.launch {
            try {
                val result = reservationRepository.getUserReservations("current_user_id_placeholder") // Reemplazar con el ID real
                _reservations.value = result
            } catch (e: Exception) {
                _reservations.value = emptyList()
            }
        }
    }

    fun deleteReservation(reservationId: String) {
        viewModelScope.launch {
            try {
                reservationRepository.deleteReservation(reservationId)
                fetchReservations() // Actualizar la lista después de eliminar
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }



    /**
     * Realiza una reserva para un producto específico.
     */
    fun reserveProduct(reservation: Reservation, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = reservationRepository.createReservation(reservation)
                if (result) {
                    fetchReservations() // Actualizar las reservas después de realizar una nueva
                }
                onResult(result)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }


    /**
     * Actualiza la ubicación actual del usuario.
     */
    fun updateUserLocation() {
        viewModelScope.launch {
            try {
                // TODO: Implementar lógica para obtener la ubicación del dispositivo.
                // Como ejemplo, se asigna una ubicación fija (Bogotá).
                _userLocation.value = Pair(4.6, -74.1)
            } catch (e: Exception) {
                _userLocation.value = null // Error al obtener ubicación
            }
        }
    }

    fun filterStoresByName(query: String) {
        _stores.value = _stores.value.filter { it.name.contains(query, ignoreCase = true) }
    }


    fun logout() {
        FirebaseAuth.getInstance().signOut()
        // Opcional: Puedes resetear datos en el ViewModel si es necesario
        _reservations.value = emptyList()
        _stores.value = emptyList()
    }



}
