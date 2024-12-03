package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.domain.model.Product
import com.example.merco242.domain.model.Reservation
import com.example.merco242.viewmodel.BuyerViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    navController: NavHostController,
    storeId: String,
    buyerViewModel: BuyerViewModel
) {
    val store by buyerViewModel.getStoreById(storeId).collectAsState(initial = null)
    val products by buyerViewModel.products.collectAsState()

    LaunchedEffect(Unit) {
        buyerViewModel.fetchProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store?.name ?: "Detalles de la Tienda") }
            )
        }
    ) { padding ->
        if (store != null) {
            val storeProducts = products.filter { it.categoryId == storeId }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = store!!.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Dirección: ${store!!.address}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (storeProducts.isEmpty()) {
                    Text(
                        text = "No hay productos disponibles en esta tienda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(storeProducts.size) { index ->
                            val product = storeProducts[index]
                            ProductCardReservable(
                                product = product,
                                onReserve = {
                                    val reservation = Reservation(
                                        id = UUID.randomUUID().toString(),
                                        userId = "current_user_id_placeholder", // Reemplazar con el ID del usuario actual.
                                        storeId = storeId,
                                        storeName = store!!.name,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    buyerViewModel.reserveProduct(reservation) { success ->
                                        if (success) {
                                            navController.popBackStack()
                                        } else {
                                            // Manejo de error, por ejemplo: mostrar un Snackbar.
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCardReservable(product: Product, onReserve: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Precio: $${product.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onReserve) {
                Text("Reservar")
            }
        }
    }
}
