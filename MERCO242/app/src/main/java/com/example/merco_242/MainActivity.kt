package com.example.merco_242

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco_242.model.User
import com.example.merco_242.viewmodel.ProfileViewModel
import com.example.merco_242.viewmodel.SignupViewModel
import com.example.merco_242.ui.theme.MERCO242Theme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MERCO242Theme {
                App()
            }
        }
    }
}


@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") { ProfileScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavController, authViewModel: SignupViewModel = viewModel()) {
    val authState by authViewModel.authState.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        TextField(value = email, onValueChange = { email = it })
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        if(authState == 1){
            CircularProgressIndicator()
        }else if(authState == 2){
            Text(text = "Hubo un error, que no podemos ver todavia")
        }else if (authState == 3){
            navController.navigate("profile")
        }

        Button(onClick = {
            authViewModel.signin(email, password)
        }) {
            Text(text = "Iniciar sesion")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel = viewModel()) {

    val userState by profileViewModel.user.observeAsState()

    LaunchedEffect(true) {
        profileViewModel.getCurrentUser()
    }

    if (userState == null) {
        navController.navigate("login")
    } else {
        // Layout que mostrará la información del usuario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Muestra los datos de usuario, revisando que no sean nulos
            Text(text = "Bienvenido, ${userState?.name ?: "Usuario"}!", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Nombre de usuario: ${userState?.username ?: "No disponible"}")
            Text(text = "Correo electrónico: ${userState?.email ?: "No disponible"}")

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            // Botones para funcionalidades adicionales
            Button(onClick = { profileViewModel.funcion1() }) {
                Text(text = "Función 1")
            }
            Button(onClick = { profileViewModel.funcion2() }) {
                Text(text = "Función 2")
            }
            Button(onClick = { profileViewModel.funcion3() }) {
                Text(text = "Función 3")
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            // Botón para cerrar sesión
            Button(onClick = {
                Firebase.auth.signOut()
                navController.navigate("login")
            }) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}

@Composable
fun SignupScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {


    val authState by signupViewModel.authState.observeAsState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextField(value = name, onValueChange = { name = it })
            TextField(value = username, onValueChange = { username = it })
            TextField(value = email, onValueChange = { email = it })
            TextField(value = password, onValueChange = { password = it })
            if (authState == 1) {
                CircularProgressIndicator()
            } else if (authState == 2) {
                Text("Hubo un error", color = Color.Red)
            }else if(authState == 3){
                navController.navigate("profile")
            }
            Button(onClick = {
                signupViewModel.signup(
                    User("", name, username, email),
                    password
                )
            }) {
                Text(text = "Registrarse")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MERCO242Theme {
        Greeting("Android")
    }
}