package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.merco242.domain.model.Store
import com.example.merco242.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreListScreen(navController: NavController, storeViewModel: StoreViewModel) {
    val stores by storeViewModel.stores.collectAsState()

    LaunchedEffect(Unit) {
        storeViewModel.fetchStores()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tiendas Disponibles") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(stores.size) { index ->
                val store = stores[index]
                StoreItem(store) {
                    navController.navigate("store_detail/${store.id}")
                }
            }
        }
    }
}

@Composable
fun StoreItem(store: Store, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = store.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = store.address)
        }
    }
}
