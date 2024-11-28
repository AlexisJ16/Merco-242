package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.domain.model.Product
import com.example.merco242.viewmodel.SellerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavHostController,
    categoryId: String,
    sellerViewModel: SellerViewModel
) {
    val products by sellerViewModel.products.collectAsState()
    val categoryProducts = products.filter { it.categoryId == categoryId }
    var newCategoryName by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de Categoría", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFD32F2F)),
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Atrás", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("Nuevo Nombre de la Categoría") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFCDD2),
                    focusedLabelColor = Color(0xFFD32F2F)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Button(
                onClick = {
                    sellerViewModel.updateCategoryName(categoryId, newCategoryName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Actualizar Nombre", color = Color.White)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(categoryProducts.size) { index ->
                    val product = categoryProducts[index]
                    ProductCard(product, onDelete = {
                        sellerViewModel.deleteProduct(product.id)
                    })
                }
            }
            Button(
                onClick = { showDeleteConfirmation = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Borrar Categoría", color = Color.White)
            }
        }

        // Ventana de Confirmación para Borrar Categoría
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirmar Eliminación") },
                text = {
                    Text(
                        "¿Estás seguro de que deseas borrar esta categoría? Esto eliminará todos los productos asociados.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        sellerViewModel.deleteCategory(categoryId)
                        navController.popBackStack()
                        showDeleteConfirmation = false
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancelar", color = Color(0xFFD32F2F))
                    }
                }
            )
        }
    }
}

@Composable
fun ProductCard(product: Product, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "Precio: $${product.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.size(90.dp, 40.dp)
            ) {
                Text("Eliminar", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
