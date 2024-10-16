package com.example.icesiapp242.service

import com.example.icesiapp242.domain.model.Seller
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface SellerServices {
    suspend fun createSeller(seller: Seller)
    suspend fun getSellerById(id: String): Seller?
}

class SellerServicesImpl : SellerServices {
    override suspend fun createSeller(seller: Seller) {
        Firebase.firestore
            .collection("sellers")
            .document(seller.id)
            .set(seller)
            .await()
    }

    override suspend fun getSellerById(id: String): Seller? {
        val seller = Firebase.firestore
            .collection("sellers")
            .document(id)
            .get()
            .await()
        return seller.toObject(Seller::class.java)
    }
}
