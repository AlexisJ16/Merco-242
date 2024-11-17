package com.example.merco242.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseInitializer {
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                FirebaseApp.initializeApp(context)
                isInitialized = true
                Log.i("FirebaseInitializer", "Firebase initialized successfully")
            } catch (e: Exception) {
                Log.e("FirebaseInitializer", "Error initializing Firebase: ${e.message}")
            }
        }
    }

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
}
