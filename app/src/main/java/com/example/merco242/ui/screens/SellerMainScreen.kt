package com.example.merco242.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.R
import com.example.merco242.domain.model.Category
import com.example.merco242.viewmodel.SellerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerMainScreen(navController: NavHostController, sellerViewModel: SellerViewModel) {
    val categories by sellerViewModel.categories.collectAsState()
    val products by sellerViewModel.products.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Tienda", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFD32F2F)),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Abrir Menú", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Crear Categoría") },
                            onClick = {
                                showMenu = false
                                navController.navigate("add_category")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Agregar Producto") },
                            onClick = {
                                showMenu = false
                                navController.navigate("add_product")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            onClick = {
                                showMenu = false
                                navController.navigate("profile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Pedidos") },
                            onClick = {
                                showMenu = false
                                navController.navigate("orders")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda") },
                            onClick = {
                                showMenu = false
                                navController.navigate("help")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                showMenu = false
                                navController.navigate("login")
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a tu tienda",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (categories.isEmpty()) {
                    Text(
                        text = "No tienes categorías aún. ¡Crea una nueva categoría!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories.size) { index ->
                            val category = categories[index]
                            val productCount = products.count { it.categoryId == category.id }
                            CategoryCard(category, productCount) {
                                navController.navigate("category_detail/${category.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, productCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = "Icono de Categoría",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "Productos disponibles: $productCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
