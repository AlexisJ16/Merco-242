package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.merco242.utils.FirebaseInitializer
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(3000)
        val currentUser = FirebaseInitializer.auth.currentUser
        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Determina el tipo de usuario y redirige a la pantalla correspondiente
            val userType = getUserType(currentUser.uid)
            val destination = if (userType == "buyer") "buyer_main" else "seller_main"
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bienvenido a Merco242", style = MaterialTheme.typography.headlineMedium)
    }
}

private fun getUserType(uid: String): String {
    // Placeholder: Implementa la lógica real para determinar el tipo de usuario.
    return "buyer"
}

