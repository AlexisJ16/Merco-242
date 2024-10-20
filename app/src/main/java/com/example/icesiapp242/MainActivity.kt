package com.example.icesiapp242

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icesiapp242.domain.model.User
import com.example.icesiapp242.ui.theme.IcesiAPP242Theme
import com.example.icesiapp242.ui.theme.White
import com.example.icesiapp242.viewmodel.ProfileViewModel
import com.example.icesiapp242.viewmodel.SignupViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IcesiAPP242Theme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
   /* NavHost(navController = navController, startDestination = "profile") {
        composable("profile") { ProfileScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController) }
    }*/
    //PARA MI PRUEBA Y HACER QUE INICIE HACIENDO UN REGISSTRO
    NavHost(navController = navController, startDestination = "signup") {
        composable("profile") { ProfileScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController) }
    }
}
@Composable
fun SignupScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {

    val authState by signupViewModel.authState.observeAsState()

    // Declarar las variables para los nuevos campos
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var celphone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Centramos horizontalmente
        ) {
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo para el nombre
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo para el apellido
            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Apellido") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo para el número de celular
            TextField(
                value = celphone,
                onValueChange = { celphone = it },
                label = { Text("Celular") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // Solo números
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo para el correo electrónico
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Teclado de email
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Campo para la contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Mostrar estado de autenticación
            if (authState == 1) {
                CircularProgressIndicator()
            } else if (authState == 2) {
                Text("Hubo un error", color = Color.Red)
            } else if (authState == 3) {
                navController.navigate("profile")
            }

            // Botón de registro
            Button(
                onClick = {
                    signupViewModel.signup(
                        User("", name, lastname, celphone, email),
                        password
                    )
                },

                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .height(50.dp), // Ajustar tamaño del botón
                shape = RoundedCornerShape(24.dp) // Esquinas redondeadas
            ) {
                Text(text = "Regístrate", color = Color.White)
            }

            // Opción para iniciar sesión si ya tiene cuenta
            TextButton(
                onClick = { /* Navegar a la pantalla de login */ },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "¿Ya tienes una cuenta?", color = Color.Gray)
            }

            // Sección para registrar con Google o Facebook
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("O regístrate con una cuenta de:")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                /*
                IconButton(onClick = { /* Registro con Google */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        tint = Color.Unspecified // Para usar el color original
                    )
                }
                IconButton(onClick = { /* Registro con Facebook */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        tint = Color.Unspecified
                    )
                }*/
            }
        }
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
    Log.e(">>>", userState.toString())
    val username by remember { mutableStateOf("") }


    LaunchedEffect(true) {
        profileViewModel.getCurrentUser()
    }
    if(userState == null){
        navController.navigate("login")
    }else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(text = "Bienvenido ${userState?.name}")

            Button(onClick = { profileViewModel.funcion1() }) {
                Text(text = "Funcion 1")
            }
            Button(onClick = { profileViewModel.funcion2() }) {
                Text(text = "Funcion 2")
            }
            Button(onClick = { profileViewModel.funcion3() }) {
                Text(text = "Funcion 3")
            }

            Button(onClick = {
                Firebase.auth.signOut() //Corregir con lo que saben
                navController.navigate("login")
            }) {
                Text(text = "Cerrar sesión")
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
    IcesiAPP242Theme {
        Greeting("Android")
    }
}