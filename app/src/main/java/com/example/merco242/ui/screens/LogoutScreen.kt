package com.example.merco242.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LogoutScreen(navController: NavHostController) {
    navController.popBackStack("login", inclusive = true)
}
