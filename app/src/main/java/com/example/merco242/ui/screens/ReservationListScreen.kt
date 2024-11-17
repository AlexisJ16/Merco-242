package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.merco242.domain.model.Reservation
import com.example.merco242.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(reservationViewModel: ReservationViewModel) {
    var reservations by remember { mutableStateOf(emptyList<Reservation>()) }

    LaunchedEffect(Unit) {
        val userId = "current_user_id_placeholder" // Obtén el usuario actual
        reservationViewModel.fetchUserReservations(userId) {
            reservations = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Reservas") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(reservations.size) { index ->
                val reservation = reservations[index]
                ReservationItem(reservation)
            }
        }
    }
}

@Composable
fun ReservationItem(reservation: Reservation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Tienda: ${reservation.storeName}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha: ${reservation.timestamp}")
        }
    }
}
