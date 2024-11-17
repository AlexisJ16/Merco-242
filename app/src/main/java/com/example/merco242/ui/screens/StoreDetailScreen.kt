package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.merco242.domain.model.Reservation
import com.example.merco242.domain.model.Store
import com.example.merco242.viewmodel.ReservationViewModel
import com.example.merco242.viewmodel.StoreViewModel

@Composable
fun StoreDetailScreen(
    navController: NavController,
    storeId: String,
    storeViewModel: StoreViewModel,
    reservationViewModel: ReservationViewModel
) {
    val stores by storeViewModel.stores.collectAsState()
    val store = stores.find { it.id == storeId }

    if (store != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = store.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = store.address)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = store.description)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val userId = "current_user_id_placeholder" // Obtén el usuario actual de tu ViewModel o Auth
                val reservation = Reservation(
                    userId = userId,
                    storeId = store.id,
                    storeName = store.name
                )
                reservationViewModel.createReservation(reservation) { success ->
                    if (success) {
                        navController.popBackStack()
                    } else {
                        // Manejar error
                    }
                }
            }) {
                Text("Reservar en esta tienda")
            }
        }
    }
}
