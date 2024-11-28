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
fun LoginScreen(
    navController: NavHostController,
    signupViewModel: SignupViewModel
) {
    // Campos para capturar datos de inicio de sesión
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Observar el estado de autenticación desde el ViewModel
    val loginState by signupViewModel.authState.observeAsState(0)
    val errorMessage by signupViewModel.errorMessage.observeAsState("")
    var userType by remember { mutableStateOf("buyer") } // Buyer como predeterminado

    // Efecto para manejar la navegación según el estado de inicio de sesión
    LaunchedEffect(loginState) {
        when (loginState) {
            3 -> { // Inicio de sesión exitoso
                val destination = if (signupViewModel.selectedUserType == "buyer") {
                    "buyer_main"
                } else {
                    "seller_main"
                }
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                }
            }
            2 -> { /* Error, mensaje ya reflejado en la UI */ }
        }
    }

    // UI del formulario de inicio de sesión
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensaje de error
        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
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

        // Selección del tipo de usuario
        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tipo de Usuario: ${userType.capitalize()}")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Buyer") },
                    onClick = {
                        userType = "buyer"
                        signupViewModel.selectedUserType = "buyer"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Seller") },
                    onClick = {
                        userType = "seller"
                        signupViewModel.selectedUserType = "seller"
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar sesión
        Button(
            onClick = {
                signupViewModel.loginUser(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para redirigir al registro
        TextButton(
            onClick = {
                navController.navigate("register")
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
