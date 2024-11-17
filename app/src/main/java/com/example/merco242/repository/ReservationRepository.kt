package com.example.merco242.repository

import com.example.merco242.domain.model.Reservation
import com.example.merco242.service.ReservationService
import com.example.merco242.service.ReservationServiceImpl

interface ReservationRepository {
    suspend fun createReservation(reservation: Reservation): Boolean
    suspend fun getUserReservations(userId: String): List<Reservation>
}

class ReservationRepositoryImpl(
    private val reservationService: ReservationService = ReservationServiceImpl()
) : ReservationRepository {
    override suspend fun createReservation(reservation: Reservation): Boolean {
        return reservationService.createReservation(reservation)
    }

    override suspend fun getUserReservations(userId: String): List<Reservation> {
        return reservationService.getUserReservations(userId)
    }
}
