package com.example.merco242.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.merco242.viewmodel.SignupViewModel

@Composable
fun LoginScreen(navController: NavHostController, signupViewModel: SignupViewModel) {
    // Variables para los campos de entrada
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("buyer") }
    var expanded by remember { mutableStateOf(false) } // Estado para el menú desplegable

    // Observar los estados del ViewModel
    val loginState by signupViewModel.authState.observeAsState(0)
    val errorMessage by signupViewModel.errorMessage.observeAsState("")

    // Efecto para manejar la navegación según el estado de autenticación
    LaunchedEffect(loginState) {
        when (loginState) {
            3 -> { // Inicio de sesión exitoso
                val destination = if (userType == "buyer") "buyer_main" else "seller_main"
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                }
            }
            2 -> { /* Manejo de errores ya se refleja en la UI */ }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensaje de error si existe
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Menú desplegable para tipo de usuario
        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tipo de Usuario: $userType")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Buyer") },
                    onClick = {
                        userType = "buyer"
                        signupViewModel.selectedUserType = "buyer" // Actualizar en el ViewModel
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Seller") },
                    onClick = {
                        userType = "seller"
                        signupViewModel.selectedUserType = "seller" // Actualizar en el ViewModel
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón de inicio de sesión
        Button(
            onClick = { signupViewModel.loginUser(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para redirigir al registro
        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
