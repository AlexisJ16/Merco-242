package com.example.merco242

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco242.ui.screens.*
import com.example.merco242.ui.theme.Merco242Theme
import com.example.merco242.viewmodel.*

class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()
    private val sellerViewModel: SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Merco242Theme {
                AppNavigation(
                    signupViewModel = signupViewModel,
                    sellerViewModel = sellerViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    signupViewModel: SignupViewModel,
    sellerViewModel: SellerViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, signupViewModel) }
        composable("register") { RegisterScreen(navController, signupViewModel) }
        composable("buyer_main") { BuyerMainScreen(navController) }
        composable("seller_main") { SellerMainScreen(navController, sellerViewModel) }
        composable("add_category") { AddCategoryScreen(navController, sellerViewModel) }
        composable("add_product") { AddProductScreen(navController, sellerViewModel) }
        composable("profile") { ProfileScreen() }
        composable("orders") { OrdersScreen() }
        composable("help") { HelpScreen() }
        composable("logout") { LogoutScreen(navController) }
    }
}

