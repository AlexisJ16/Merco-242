package com.example.merco242

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco242.ui.theme.Merco242Theme
import com.example.merco242.viewmodel.SignupViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            Merco242Theme {
                App(onGoogleSignIn = { signInWithGoogle() })
            }
        }

        signupViewModel.authState.observe(this) { authState ->
            when (authState) {
                1 -> { /* Show progress */ }
                2 -> Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
                3 -> { /* Navigate to profile */ }
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
                    user?.let {
                        signupViewModel.loadUserDetails(it.uid) // Ahora pasamos el userId correctamente
                    }
                } else {
                    Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun App(onGoogleSignIn: () -> Unit) {
    val navController = rememberNavController()
    val signupViewModel: SignupViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("user_selection") { UserSelectionScreen(navController, signupViewModel, onGoogleSignIn) }
        composable("login") { LoginScreen(navController, signupViewModel) }
        composable("buyer_dashboard") { BuyerDashboard(navController, signupViewModel) }
        composable("seller_dashboard") { SellerDashboard(navController, signupViewModel) }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("user_selection")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_merco),
            contentDescription = "Merco Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
fun UserSelectionScreen(navController: NavController, signupViewModel: SignupViewModel, onGoogleSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Quién eres?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                signupViewModel.setUserType("buyer")
                navController.navigate("login")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "COMPRADOR", color = Color.White)
        }

        Button(
            onClick = {
                signupViewModel.setUserType("seller")
                navController.navigate("login")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "VENDEDOR", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("signup") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Crear una cuenta", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "O inicia sesión con:", color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = onGoogleSignIn) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Sign-In",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, signupViewModel: SignupViewModel) {
    val authState by signupViewModel.authState.observeAsState()
    val errorMessage by signupViewModel.errorMessage.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { signupViewModel.loginUser(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Iniciar sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            1 -> CircularProgressIndicator()
            2 -> errorMessage?.let { Text(text = it, color = Color.Red) }
            3 -> {
                when (signupViewModel.selectedUserType) {
                    "buyer" -> navController.navigate("buyer_dashboard")
                    "seller" -> navController.navigate("seller_dashboard")
                }
            }
        }
    }
}

@Composable
fun BuyerDashboard(navController: NavController, signupViewModel: SignupViewModel) {
    val userDetails by signupViewModel.userDetails.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido Buyer", style = MaterialTheme.typography.headlineSmall)

        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    signupViewModel.loadUserDetails(userId) // Llamada para cargar detalles de usuario
                }
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Mostrar Resumen de Usuario")
        }

        userDetails?.let { user ->
            Text("Nombre: ${user.name}")
            Text("Apellido: ${user.lastname}")
            Text("Celular: ${user.celphone}")
            Text("Email: ${user.email}")
        }
    }
}


@Composable
fun SellerDashboard(navController: NavController, signupViewModel: SignupViewModel) {
    val userDetails by signupViewModel.userDetails.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido Seller", style = MaterialTheme.typography.headlineSmall)

        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    signupViewModel.loadUserDetails(userId) // Llamada para cargar detalles de usuario
                }
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Mostrar Resumen de Usuario")
        }

        userDetails?.let { user ->
            Text("Nombre: ${user.name}")
            Text("Apellido: ${user.lastname}")
            Text("Celular: ${user.celphone}")
            Text("Email: ${user.email}")
        }
    }
}
