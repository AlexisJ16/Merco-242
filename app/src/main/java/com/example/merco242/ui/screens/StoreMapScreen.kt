package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.merco242.domain.model.Store
import com.example.merco242.viewmodel.StoreViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun StoreMapScreen(storeViewModel: StoreViewModel) {
    val stores by storeViewModel.stores.collectAsState()
    val context = LocalContext.current

    // Estado inicial de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.6, -74.1), 10f) // Bogotá como punto inicial
    }

    // Efecto para cargar las tiendas al iniciar la pantalla
    LaunchedEffect(Unit) {
        storeViewModel.fetchStores()
    }

    // Mapa de Google Maps
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Agregar marcadores para cada tienda
        stores.forEach { store ->
            Marker(
                state = MarkerState(position = LatLng(store.latitude, store.longitude)),
                title = store.name,
                snippet = store.address
            )
        }
    }
}
