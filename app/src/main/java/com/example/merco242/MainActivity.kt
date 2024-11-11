package com.example.merco242

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("user_selection") { UserSelectionScreen(navController, onGoogleSignIn) }
        composable("login") { LoginScreen(navController, onGoogleSignIn) }
        composable("signup") { SignupScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("buyer_dashboard") { BuyerDashboard(navController) }
        composable("seller_dashboard") { SellerDashboard(navController) }
        composable("map_screen") { MapScreen() }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds
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
fun UserSelectionScreen(navController: NavController, onGoogleSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Quien eres?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "COMPRADOR", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("login") },
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

        Text(text = "O inicia sesión con:", color = Color.Black, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onGoogleSignIn) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign-In",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun MapScreen() {
    // Implementación de Google Maps
    // Aquí se mostraría el mapa con la ubicación de tiendas cercanas
    Text(text = "Pantalla del mapa - Muestra tiendas cercanas aquí", modifier = Modifier.fillMaxSize())
}

@Composable
fun LoginScreen(
    navController: NavController,
    onGoogleSignIn: () -> Unit = {},
    authViewModel: SignupViewModel = viewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar sesión", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), fontSize = 24.sp)

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
            onClick = { authViewModel.loginUser(email, password, "buyer") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Iniciar sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "O inicia sesión con:",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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