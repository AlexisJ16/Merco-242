package com.example.merco242

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco242.domain.model.User
import com.example.merco242.ui.theme.Merco242Theme
import com.example.merco242.viewmodel.ProfileViewModel
import com.example.merco242.viewmodel.SignupViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    // Activity Result Launcher para manejar el resultado de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            Merco242Theme {
                App(onGoogleSignIn = { signInWithGoogle() }) // Paso de onGoogleSignIn a App
            }
        }

        // Observación del estado de autenticación y manejo de errores
        signupViewModel.authState.observe(this) { authState ->
            when (authState) {
                1 -> { /* Muestra progreso en MainActivity o delega al Composable */ }
                2 -> Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
                3 -> { /* Navega a otra pantalla, por ejemplo, al perfil */ }
            }
        }

        signupViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    signupViewModel.updateAuthState(user)
                } else {
                    Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun App(onGoogleSignIn: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("profile") { ProfileScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController, onGoogleSignIn) } // Paso de onGoogleSignIn a LoginScreen
        composable("buyer_dashboard") { BuyerDashboard(navController) }
        composable("seller_dashboard") { SellerDashboard(navController) }
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
    var selectedUserType by remember { mutableStateOf("buyer") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))

        TextField(value = name, onValueChange = { name = it }, label = { Text("First Name") })
        TextField(value = lastname, onValueChange = { lastname = it }, label = { Text("Last Name") })
        TextField(value = celphone, onValueChange = { celphone = it }, label = { Text("Cellphone") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { selectedUserType = "buyer" }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedUserType == "buyer") Color.Green else Color.Gray)) {
                Text(text = "Buyer")
            }
            Button(onClick = { selectedUserType = "seller" }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedUserType == "seller") Color.Green else Color.Gray)) {
                Text(text = "Seller")
            }
        }

        Button(onClick = {
            signupViewModel.registerUser(User("", name, lastname, celphone, email), password, selectedUserType)
        }) {
            Text(text = "Register")
        }

        when (authState) {
            1 -> CircularProgressIndicator()
            2 -> Text(text = "Error occurred", color = Color.Red)
            3 -> navController.navigate("profile")
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavController,
    onGoogleSignIn: () -> Unit = {}, // Se define como parámetro opcional para evitar errores
    authViewModel: SignupViewModel = viewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf("buyer") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 24.dp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(vertical = 16.dp)) {
            Button(onClick = { selectedUserType = "buyer" }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedUserType == "buyer") Color.Green else Color.Gray)) {
                Text(text = "Buyer")
            }
            Button(onClick = { selectedUserType = "seller" }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedUserType == "seller") Color.Green else Color.Gray)) {
                Text(text = "Seller")
            }
        }

        Button(onClick = { authViewModel.loginUser(email, password, selectedUserType) }) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGoogleSignIn, // Llamada a la función onGoogleSignIn
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign in with Google", color = Color.Black)
        }

        when (authState) {
            1 -> CircularProgressIndicator()
            2 -> Text(text = errorMessage ?: "Authentication error", color = Color.Red)
            3 -> {
                if (selectedUserType == "buyer") {
                    navController.navigate("buyer_dashboard")
                } else {
                    navController.navigate("seller_dashboard")
                }
            }
        }

        Text(
            text = "New user? Register here",
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 24.dp)
                .clickable { navController.navigate("signup") }
        )
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
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(text = "Welcome ${userState?.name}")

            Button(onClick = { profileViewModel.funcion1() }) { Text(text = "Function 1") }
            Button(onClick = { profileViewModel.funcion2() }) { Text(text = "Function 2") }
            Button(onClick = { profileViewModel.funcion3() }) { Text(text = "Function 3") }

            Button(onClick = {
                Firebase.auth.signOut()
                navController.navigate("login")
            }) { Text(text = "Log Out") }
        }
    }
}

@Composable
fun BuyerDashboard(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome Buyer", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("login")
        }) {
            Text(text = "Log Out")
        }
    }
}

@Composable
fun SellerDashboard(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome Seller", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("login")
        }) {
            Text(text = "Log Out")
        }
    }
}