package com.example.merco242.service

import android.util.Log
import com.example.merco242.domain.model.Reservation
import com.example.merco242.utils.FirebaseInitializer
import kotlinx.coroutines.tasks.await

interface ReservationService {
    suspend fun createReservation(reservation: Reservation): Boolean
    suspend fun getUserReservations(userId: String): List<Reservation>
}

class ReservationServiceImpl : ReservationService {
    private val db = FirebaseInitializer.firestore

    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            val collection = db.collection("reservations")
            collection.add(reservation).await()
            true
        } catch (e: Exception) {
            Log.e("ReservationServiceImpl", "Error creando reserva: ${e.message}")
            false
        }
    }

    override suspend fun getUserReservations(userId: String): List<Reservation> {
        return try {
            val documents = db.collection("reservations").whereEqualTo("userId", userId).get().await()
            documents.documents.mapNotNull { it.toObject(Reservation::class.java) }
        } catch (e: Exception) {
            Log.e("ReservationServiceImpl", "Error obteniendo reservas: ${e.message}")
            emptyList()
        }
    }
}
