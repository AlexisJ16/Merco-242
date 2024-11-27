package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.domain.model.Category
import com.example.merco242.domain.model.Product
import com.example.merco242.viewmodel.SellerViewModel

@Composable
fun AddProductScreen(navController: NavHostController, sellerViewModel: SellerViewModel) {
    val categories by sellerViewModel.categories.collectAsState()
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Agregar Nuevo Producto",
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Nombre del Producto") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        // Dropdown para seleccionar una categoría
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (selectedCategoryId.isEmpty()) "Selecciona una Categoría" else selectedCategoryId)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategoryId = category.id
                            expanded = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("URL de la Imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val product = Product(
                    id = java.util.UUID.randomUUID().toString(),
                    name = productName,
                    description = productDescription,
                    price = productPrice.toDoubleOrNull() ?: 0.0,
                    categoryId = selectedCategoryId,
                    imageUrl = imageUrl
                )
                sellerViewModel.addProduct(product) { success ->
                    if (success) {
                        navController.popBackStack() // Volver a la pantalla principal
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }
    }
}
