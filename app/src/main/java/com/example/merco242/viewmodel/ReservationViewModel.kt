package com.example.merco242.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merco242.domain.model.Reservation
import com.example.merco242.repository.ReservationRepository
import com.example.merco242.repository.ReservationRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val reservationRepository: ReservationRepository = ReservationRepositoryImpl()
) : ViewModel() {

    fun createReservation(reservation: Reservation, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = reservationRepository.createReservation(reservation)
            onResult(result)
        }
    }

    fun fetchUserReservations(userId: String, onResult: (List<Reservation>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = reservationRepository.getUserReservations(userId)
            onResult(result)
        }
    }
}
