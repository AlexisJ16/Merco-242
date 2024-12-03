package com.example.merco242.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.R
import com.example.merco242.domain.model.Product
import com.example.merco242.viewmodel.BuyerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerMainScreen(
    navController: NavHostController,
    buyerViewModel: BuyerViewModel
) {
    val stores by buyerViewModel.stores.collectAsState()
    val products by buyerViewModel.products.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    // Cargar datos
    LaunchedEffect(Unit) {
        buyerViewModel.fetchStores()
        buyerViewModel.fetchProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos cercanos a ti", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi perfil") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            onClick = {
                                navController.navigate("profile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Favoritos") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                // Implementar navegación para favoritos
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mis pedidos") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.ShoppingBag,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                navController.navigate("reservation_list")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda") },
                            leadingIcon = { Icon(Icons.Filled.Help, contentDescription = null) },
                            onClick = {
                                navController.navigate("help")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.ExitToApp,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                buyerViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onSearch = { query ->
                    buyerViewModel.filterStoresByName(query)
                }
            )

            // Mostrar lista de tiendas y productos destacados
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tiendas
                item {
                    Text(
                        text = "Ver todas las tiendas >",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            navController.navigate("store_list")
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Productos destacados (Agrupados por categoría)
                items(products.groupBy { it.categoryId }.toList()) { (category, categoryProducts) ->
                    Text(
                        text = category, // Asume que `category` es el nombre de la categoría
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categoryProducts) { product ->
                            ProductCard(product) {
                                // Manejar clic en el producto (Por ejemplo, realizar una reserva)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            onSearch(query)
        },
        label = { Text("Buscar") },
        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
        modifier = modifier
    )
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder), // Cambiar a la imagen del producto
                contentDescription = product.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Descuento: ${product.price}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
