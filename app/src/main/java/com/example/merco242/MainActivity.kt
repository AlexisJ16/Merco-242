package com.example.merco242

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.merco242.ui.screens.*
import com.example.merco242.ui.theme.Merco242Theme
import com.example.merco242.utils.FirebaseInitializer
import com.example.merco242.viewmodel.*

class MainActivity : ComponentActivity() {

    private val signupViewModel: SignupViewModel by viewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private val reservationViewModel: ReservationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseInitializer.initialize(this)
        requestLocationPermission()

        setContent {
            Merco242Theme {
                AppNavigation(
                    signupViewModel = signupViewModel,
                    storeViewModel = storeViewModel,
                    reservationViewModel = reservationViewModel
                )
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    companion object {
        const val LOCATION_REQUEST_CODE = 100
    }
}

@Composable
fun AppNavigation(
    signupViewModel: SignupViewModel,
    storeViewModel: StoreViewModel,
    reservationViewModel: ReservationViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, signupViewModel) }
        composable("register") { RegisterScreen(navController, signupViewModel) }
        composable("buyer_main") { BuyerMainScreen(navController) }
        composable("seller_main") { SellerMainScreen(navController) }
    }
}
