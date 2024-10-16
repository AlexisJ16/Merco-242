package com.example.icesiapp242.repository

import com.example.icesiapp242.domain.model.Seller
import com.example.icesiapp242.service.SellerServices
import com.example.icesiapp242.service.SellerServicesImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface SellerRepository {
    suspend fun createSeller(seller: Seller)
    suspend fun getCurrentSeller(): Seller?
}

class SellerRepositoryImpl(
    val sellerServices: SellerServices = SellerServicesImpl()
) : SellerRepository {

    override suspend fun createSeller(seller: Seller) {
        sellerServices.createSeller(seller)
    }

    override suspend fun getCurrentSeller(): Seller? {
        Firebase.auth.currentUser?.let {
            return sellerServices.getSellerById(it.uid)
        } ?: run {
            return null
        }
    }
}