package com.example.merco242


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco242.domain.model.User
import com.example.merco242.ui.theme.Merco242Theme
//import com.example.merco242.ui.theme.White
import com.example.merco242.viewmodel.ProfileViewModel
import com.example.merco242.viewmodel.SignupViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Merco242Theme   {
                App()
            }
        }
    }
 */
class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observar el authState y mostrar mensajes de error o manejar navegación
        signupViewModel.authState.observe(this) { authState ->
            when (authState) {
                1 -> {
                    // Mostrar progreso en MainActivity o delegar en el Composable
                }
                2 -> {
                    // Mostrar un mensaje de error o manejarlo de alguna forma
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    // Navegar a otra pantalla (por ejemplo, Profile) si la autenticación fue exitosa
                }
            }
        }

        signupViewModel.errorMessage.observe(this) { errorMessage ->
            // Mostrar el mensaje de error si existe
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        enableEdgeToEdge()
        setContent {
            Merco242Theme {
                App()
            }
        }
    }
}



@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("profile") { ProfileScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("comprador_dashboard") { CompradorDashboard(navController) }
        composable("vendedor_dashboard") { VendedorDashboard(navController) }
    }

}


@Composable
fun SignupScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {
    val authState by signupViewModel.authState.observeAsState()
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var celphone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf("comprador") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrarse", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))

        // Campos de registro
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        TextField(value = lastname, onValueChange = { lastname = it }, label = { Text("Apellido") })
        TextField(value = celphone, onValueChange = { celphone = it }, label = { Text("Celular") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())

        // Botones de tipo de usuario
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { selectedUserType = "comprador" }) { Text(text = "Comprador") }
            Button(onClick = { selectedUserType = "vendedor" }) { Text(text = "Vendedor") }
        }

        // Botón de registro
        Button(onClick = {
            signupViewModel.signup(User("", name, lastname, celphone, email), password, selectedUserType)
        }) { Text(text = "Regístrate") }

        // Mostrar estado de autenticación
        when (authState) {
            1 -> CircularProgressIndicator()
            2 -> Text(text = "Hubo un error", color = Color.Red)
            3 -> navController.navigate("profile")
        }
    }
}




@Composable
fun LoginScreen(navController: NavController, authViewModel: SignupViewModel = viewModel()) {
    val authState by authViewModel.authState.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf("comprador") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Inicio de sesión", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))

        // Campos de Email y Contraseña
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())

        // Botones de tipo de usuario
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(vertical = 16.dp)) {
            Button(onClick = { selectedUserType = "comprador" }) { Text(text = "Comprador") }
            Button(onClick = { selectedUserType = "vendedor" }) { Text(text = "Vendedor") }
        }

        // Iniciar sesión
        Button(onClick = {
            authViewModel.signinWithUserType(email, password, selectedUserType)
        }) { Text(text = "Iniciar Sesión") }

        // Proceso de autenticación
        when (authState) {
            1 -> CircularProgressIndicator()
            2 -> Text(text = errorMessage ?: "Error de autenticación", color = Color.Red)
            3 -> {
                if (selectedUserType == "comprador") {
                    navController.navigate("comprador_dashboard")
                } else {
                    navController.navigate("vendedor_dashboard")
                }
            }
        }

        // Opción para registro
        Text(
            text = "¿Nuevo en la app?, Regístrate aquí",
            color = Color.Black,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { navController.navigate("signup") }
        )
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
fun CompradorDashboard(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido Comprador", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("login")
        }) {
            Text(text = "Cerrar sesión")
        }
    }
}

@Composable
fun VendedorDashboard(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido Vendedor", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("login")
        }) {
            Text(text = "Cerrar sesión")
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
    Merco242Theme {
        Greeting("Android")
    }
}