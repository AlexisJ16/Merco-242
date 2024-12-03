package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.domain.model.Category
import com.example.merco242.viewmodel.SellerViewModel

@Composable
fun AddCategoryScreen(navController: NavHostController, sellerViewModel: SellerViewModel) {
    var categoryName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Crear Nueva Categoría",
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Nombre de la Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val category = Category(
                    id = java.util.UUID.randomUUID().toString(),
                    name = categoryName
                )
                sellerViewModel.addCategory(category) { success ->
                    if (success) {
                        navController.popBackStack() // Regresa a la pantalla principal
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Categoría")
        }
    }
}
