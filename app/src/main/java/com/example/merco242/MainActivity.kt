package com.example.merco242

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.merco242.ui.screens.BuyerMainScreen
import com.example.merco242.ui.screens.CreateStoreScreen
import com.example.merco242.ui.screens.EditStoreDetailScreen
import com.example.merco242.ui.screens.EditStoreScreen
import com.example.merco242.ui.screens.HelpScreen
import com.example.merco242.ui.screens.LoginScreen
import com.example.merco242.ui.screens.RegisterScreen
import com.example.merco242.ui.screens.ReservationListScreen
import com.example.merco242.ui.screens.SellerMainScreen
import com.example.merco242.ui.screens.SplashScreen
import com.example.merco242.ui.screens.StoreDetailScreen
import com.example.merco242.ui.theme.Merco242Theme
import com.example.merco242.viewmodel.BuyerViewModel
import com.example.merco242.viewmodel.SellerViewModel
import com.example.merco242.viewmodel.SignupViewModel

class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()
    private val sellerViewModel: SellerViewModel by viewModels()
    private val buyerViewModel: BuyerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Merco242Theme {
                AppNavigation(
                    signupViewModel = signupViewModel,
                    sellerViewModel = sellerViewModel,
                    buyerViewModel = buyerViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    signupViewModel: SignupViewModel,
    sellerViewModel: SellerViewModel,
    buyerViewModel: BuyerViewModel
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        signupViewModel.fetchUserType { userType ->
            val isLoggedIn = signupViewModel.isLoggedIn()

            if (isLoggedIn) {
                val startDestination = if (userType == "buyer") "buyer_main" else "seller_main"
                navController.navigate(startDestination) {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }

        // Pantallas comunes
        composable("login") { LoginScreen(navController, signupViewModel) }
        composable("register") { RegisterScreen(navController, signupViewModel) }
        composable("help") { HelpScreen() }
        composable("logout") {
            signupViewModel.logout()
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }

        // Navegación para compradores
        navigation(startDestination = "buyer_main", route = "buyer") {
            composable("buyer_main") { BuyerMainScreen(navController, buyerViewModel) }
            composable(
                route = "store_detail/{storeId}",
                arguments = listOf(navArgument("storeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
                StoreDetailScreen(navController, storeId, buyerViewModel)
            }
            composable("reservation_list") { ReservationListScreen(buyerViewModel) }
        }

        // Navegación para vendedores
        navigation(startDestination = "seller_main", route = "seller") {
            composable("seller_main") { SellerMainScreen(navController, sellerViewModel) }
            composable("create_store") { CreateStoreScreen(navController, sellerViewModel) }
            composable("edit_store") { EditStoreScreen(navController, sellerViewModel) }
            composable(
                route = "edit_store_detail/{storeId}",
                arguments = listOf(navArgument("storeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
                EditStoreDetailScreen(
                    navController,
                    sellerViewModel,
                    storeId
                ) // Asegúrate de tener esta clase
            }
        }
    }
}
