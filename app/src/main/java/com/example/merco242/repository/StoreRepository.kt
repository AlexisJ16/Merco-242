package com.example.merco242.repository

import com.example.merco242.domain.model.Store
import com.example.merco242.service.StoreService
import com.example.merco242.service.StoreServiceImpl

interface StoreRepository {
    suspend fun getStores(): List<Store>
}

class StoreRepositoryImpl(
    private val storeService: StoreService = StoreServiceImpl()
) : StoreRepository {
    override suspend fun getStores(): List<Store> {
        return storeService.getStores()
    }
}
