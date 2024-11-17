package com.example.merco242.service

import android.util.Log
import com.example.merco242.domain.model.Store
import com.example.merco242.utils.FirebaseInitializer
import kotlinx.coroutines.tasks.await

interface StoreService {
    suspend fun getStores(): List<Store>
}

class StoreServiceImpl : StoreService {
    private val db = FirebaseInitializer.firestore

    override suspend fun getStores(): List<Store> {
        return try {
            val collection = db.collection("stores")
            val documents = collection.get().await()
            documents.documents.mapNotNull { it.toObject(Store::class.java) }
        } catch (e: Exception) {
            Log.e("StoreServiceImpl", "Error obteniendo tiendas: ${e.message}")
            emptyList()
        }
    }
}
